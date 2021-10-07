package com.prismamp.todopago.payment.adapter.repository.kafka

import com.prismamp.todopago.commons.queues.producer.KafkaProducer
import com.prismamp.todopago.payment.adapter.repository.model.OperationToPersist
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PersistenceProducer(
    private val producerOperationToPersist: KafkaProducer<String, OperationToPersist>
) {

    @Value("\${kafka.topic.operation.executed.pos.name}")
    private lateinit var executedOperationTopic: String

    fun operationExecutedEvent(value: OperationToPersist) =
        producerOperationToPersist
            .produce(executedOperationTopic, value)

}
