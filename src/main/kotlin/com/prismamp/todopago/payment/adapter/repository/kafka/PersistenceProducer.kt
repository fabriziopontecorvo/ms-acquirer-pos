package com.prismamp.todopago.payment.adapter.repository.kafka

import com.prismamp.todopago.commons.queues.producer.KafkaProducer
import com.prismamp.todopago.payment.adapter.repository.model.OperationToPersist
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PersistenceProducer(
    private val producer: KafkaProducer<String, OperationToPersist>
) {
    @Value("\${kafka.topic.operation.executed.pos.name}")
    var topic: String = ""

    fun produce(event: OperationToPersist) =
        producer.produce(topic, event)

}
