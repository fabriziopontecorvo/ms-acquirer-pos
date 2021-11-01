package com.prismamp.todopago.payment.adapter.repository.kafka

import com.prismamp.todopago.commons.queues.producer.KafkaProducer
import com.prismamp.todopago.model.anOperationToPersist
import com.prismamp.todopago.payment.adapter.repository.model.OperationToPersist
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object PersistenceProducerSpec : Spek({

    Feature("persisted event") {

        lateinit var producer: KafkaProducer<String, OperationToPersist>
        lateinit var persistence: PersistenceProducer
        beforeEachScenario {
            producer = mockk()
            persistence = PersistenceProducer(producer)
        }

        Scenario("produce a event of persistence") {
            lateinit var event: OperationToPersist

            Given("a operation") {
                event = anOperationToPersist()
            }

            And("mock producer") {
                every { producer.produce(topic = "", value = event) } returns Unit
            }

            When("call event of persistence") {
                persistence.produce(event)
            }

            Then("produce is called") {
                verify(exactly = 1) {
                    producer.produce(topic = "", value = event)
                }
            }

        }

    }

})


