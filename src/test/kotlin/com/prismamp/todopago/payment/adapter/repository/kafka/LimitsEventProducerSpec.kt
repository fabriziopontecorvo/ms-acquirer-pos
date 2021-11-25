package com.prismamp.todopago.payment.adapter.repository.kafka

import com.prismamp.todopago.commons.queues.producer.KafkaProducer
import com.prismamp.todopago.payment.model.aNotSatisfiedLimitEvent
import com.prismamp.todopago.payment.adapter.repository.model.NotSatisfiedLimitEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object LimitsEventProducerSpec: Spek({

    Feature("produce limit event"){

        lateinit var producer: KafkaProducer<String, NotSatisfiedLimitEvent>
        lateinit var limit: LimitsEventProducer
        beforeEachScenario {
            producer = mockk()
            limit = LimitsEventProducer(producer)
        }

        Scenario("produce message"){
            lateinit var event: NotSatisfiedLimitEvent

            Given(" a Not satisfied limit event"){
                event = aNotSatisfiedLimitEvent()
            }

            And("mock producer"){
                every { producer.produce(topic = "", value = event) } returns Unit
            }

            When("call limit produce event"){
                limit.produce(event)
            }

            Then("produce a message"){
                verify(exactly = 1) { producer.produce(topic = "", value = event) }
            }

        }

    }

})
