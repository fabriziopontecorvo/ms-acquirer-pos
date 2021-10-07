package com.prismamp.todopago.configuration.kafka

import com.prismamp.todopago.configuration.Constants
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer

@EnableKafka
@Configuration
class KafkaConfiguration {

    companion object {
        val DEAD_LETTER_TOPIC = Constants.APP_NAME.plus(".exception.deadLetter.topic")
    }

    @Bean
    fun kafkaObjectMapper(): DefaultKafkaObjectMapper {
        return DefaultKafkaObjectMapper()
    }

    @Primary
    @Bean
    fun producerFactory(kafkaProperties: KafkaProperties): ProducerFactory<String, String> =
        DefaultKafkaProducerFactory(kafkaProperties.buildProducerProperties())

    @Primary
    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, String>): KafkaTemplate<String, String> =
        KafkaTemplate(producerFactory)

    @Bean("stringSerializerProducerFactory")
    fun stringProducerFactory(kafkaProperties: KafkaProperties): ProducerFactory<String, String> =
        DefaultKafkaProducerFactory(
            kafkaProperties.buildProducerProperties().apply {
                this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
                this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
            }
        )

    @Bean("stringSerializerKafkaTemplate")
    fun stringKafkaTemplate(
        @Qualifier("stringSerializerProducerFactory") producerFactory: ProducerFactory<String, String>
    ): KafkaTemplate<String, String> =
        KafkaTemplate(producerFactory)

    fun consumerDefaultFactory(kafkaProperties: KafkaProperties): ConsumerFactory<String, String> =
        DefaultKafkaConsumerFactory(kafkaProperties.buildConsumerProperties().apply {
            this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
            this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
            this[ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS] = StringDeserializer::class.java
            this[ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS] = StringDeserializer::class.java
        }
        )

    fun consumerStringDeserializerFactory(kafkaProperties: KafkaProperties): ConsumerFactory<String, String> =
        DefaultKafkaConsumerFactory(kafkaProperties.buildConsumerProperties().apply {
            this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
            this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
            this[ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS] = StringDeserializer::class.java
            this[ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS] = StringDeserializer::class.java
        }, StringDeserializer(), StringDeserializer())

    @Primary
    @Bean
    fun kafkaDefaultContainerListener(
        kafkaProperties: KafkaProperties,
        errorHandler: LoggingSeekToCurrentErrorHandler
    ): ConcurrentKafkaListenerContainerFactory<String, String> =
        ConcurrentKafkaListenerContainerFactory<String, String>()
            .apply {
                this.consumerFactory = consumerDefaultFactory(kafkaProperties)
                this.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
                this.setErrorHandler(errorHandler)
            }

    @Bean("kafkaStringDeserializerContainerListener")
    fun kafkaStringDeserializerContainerListener(
        kafkaProperties: KafkaProperties,
        errorHandlerDecorator: LoggingSeekToCurrentErrorHandler
    ): ConcurrentKafkaListenerContainerFactory<String, String> =
        ConcurrentKafkaListenerContainerFactory<String, String>()
            .apply {
                this.consumerFactory = consumerStringDeserializerFactory(kafkaProperties)
                this.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
                this.setErrorHandler(errorHandlerDecorator)
            }

    @Bean
    fun publisher(bytesTemplate: KafkaOperations<*, *>?): DeadLetterPublishingRecoverer? {
        return DeadLetterPublishingRecoverer(bytesTemplate) { cr: ConsumerRecord<*, *>, _: Exception? ->
            TopicPartition(
                DEAD_LETTER_TOPIC,
                cr.partition()
            )
        }
    }

}
