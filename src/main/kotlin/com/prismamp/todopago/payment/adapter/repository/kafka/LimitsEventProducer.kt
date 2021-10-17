package com.prismamp.todopago.payment.adapter.repository.kafka

import com.prismamp.todopago.commons.queues.producer.KafkaProducer
import com.prismamp.todopago.payment.adapter.repository.model.NotSatisfiedLimitEvent
import com.prismamp.todopago.payment.adapter.repository.model.OperationToPersist
import org.springframework.stereotype.Component

@Component
class LimitsEventProducer(
    private val producerOperationToPersist: KafkaProducer<String, NotSatisfiedLimitEvent>

) {
    fun produce(notSatisfiedLimitEvent: NotSatisfiedLimitEvent) {
        TODO("Not yet implemented")
    }

}
