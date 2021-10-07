package com.prismamp.todopago.configuration.kafka

import com.prismamp.todopago.util.logs.CompanionLogger
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.MessageListenerContainer
import org.springframework.kafka.listener.SeekToCurrentErrorHandler
import org.springframework.stereotype.Component

@Component
class LoggingSeekToCurrentErrorHandler(
    deadLetter: DeadLetterPublishingRecoverer?
) : SeekToCurrentErrorHandler(deadLetter) {

    companion object: CompanionLogger()

    override fun handle(
        thrownException: Exception?, records: List<ConsumerRecord<*, *>?>?,
        consumer: Consumer<*, *>?, container: MessageListenerContainer?
    ) {
        log {error("Error while processing: {}", thrownException?.cause?.message) }
        super.handle(thrownException, records, consumer, container)
    }
}
