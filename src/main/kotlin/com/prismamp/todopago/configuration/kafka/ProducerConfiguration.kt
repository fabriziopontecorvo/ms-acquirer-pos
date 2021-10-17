package com.prismamp.todopago.configuration.kafka

import com.prismamp.todopago.commons.queues.KafkaObjectMapper
import com.prismamp.todopago.commons.queues.QueueTenantReceiver
import com.prismamp.todopago.commons.queues.factory.factoryStrategy.SerializationStrategy
import com.prismamp.todopago.commons.queues.factory.factoryStrategy.TenantAwarenessStrategy
import com.prismamp.todopago.commons.queues.factory.messageSerializer.KafkaMessageSerializerFactory
import com.prismamp.todopago.commons.queues.factory.tenantAwareMessageGenerator.TenantAwareKafkaMessageGeneratorFactory
import com.prismamp.todopago.commons.queues.message.KafkaMessageSerializer
import com.prismamp.todopago.commons.queues.message.TenantAwareKafkaMessageGenerator
import com.prismamp.todopago.commons.queues.producer.KafkaMessageProducer
import com.prismamp.todopago.commons.queues.producer.KafkaProducer
import com.prismamp.todopago.commons.queues.producer.SerializedMessageKafkaProducerDecorator
import com.prismamp.todopago.commons.queues.producer.TenantAwareKafkaProducerDecorator
import com.prismamp.todopago.commons.queues.producerRecord.DefaultProducerRecordFactory
import com.prismamp.todopago.payment.adapter.repository.model.OperationToPersist
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component


@Component
class ProducerConfiguration {

    @Bean("tenantAwareSerializedOperationToPersistProducer")
    fun tenantAwareSerializedOperationToPersistProducer(
        tenantAwareKafkaMessageGenerator: TenantAwareKafkaMessageGenerator,
        kafkaMessageSerializer: KafkaMessageSerializer,
        kafkaTemplate: KafkaTemplate<String?, String>
    ): KafkaProducer<String, OperationToPersist> =
        TenantAwareKafkaProducerDecorator(
            tenantAwareKafkaMessageGenerator,
            SerializedMessageKafkaProducerDecorator(
                kafkaMessageSerializer,
                KafkaMessageProducer(kafkaTemplate, DefaultProducerRecordFactory())
            )
        )


    @Bean("tenantAwareSerializedNotSatisfiedLimitEventProducer")
    fun tenantAwareSerializedNotSatisfiedLimitEvent(
        tenantAwareKafkaMessageGenerator: TenantAwareKafkaMessageGenerator,
        kafkaMessageSerializer: KafkaMessageSerializer,
        kafkaTemplate: KafkaTemplate<String?, String>
    ): KafkaProducer<String, NotSatisfiedLimitEvent> =
        TenantAwareKafkaProducerDecorator(
            tenantAwareKafkaMessageGenerator,
            SerializedMessageKafkaProducerDecorator(
                kafkaMessageSerializer,
                KafkaMessageProducer(kafkaTemplate, DefaultProducerRecordFactory())
            )
        )

    @Bean
    fun tenantAwareByTopicKafkaMessageGenerator(
        tenantReceiver: QueueTenantReceiver
    ): TenantAwareKafkaMessageGenerator =
        TenantAwareKafkaMessageGeneratorFactory
            .make(
                TenantAwarenessStrategy.Topic,
                tenantReceiver
            )

    @Bean
    fun keyPayloadKafkaMessageSerializer(kafkaObjectMapper: KafkaObjectMapper): KafkaMessageSerializer =
        KafkaMessageSerializerFactory
            .make(
                SerializationStrategy.KeyAndPayload,
                kafkaObjectMapper
            )

}
