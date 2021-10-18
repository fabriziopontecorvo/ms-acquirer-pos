package com.prismamp.todopago.payment.adapter.repository.kafka

import com.prismamp.todopago.commons.queues.producer.KafkaProducer
import com.prismamp.todopago.payment.adapter.repository.model.NotSatisfiedLimitEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class LimitsEventProducer(
    private val producer: KafkaProducer<String, NotSatisfiedLimitEvent>

) {

    @Value("\${kafka.topic.limit.not-satisfied.name}")
    var topic: String = ""

    fun produce(event: NotSatisfiedLimitEvent) =
        producer.produce(topic, event)

}
