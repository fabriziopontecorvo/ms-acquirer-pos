package com.prismamp.todopago.payment.adapter.repository.dao

import aDecidirResponseReason
import aMessage
import com.prismamp.todopago.MessageConverter
import com.prismamp.todopago.payment.domain.model.GatewayResponse
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import java.util.Optional.empty
import java.util.Optional.of


object DecidirErrorConverterSpec : Spek({

    Feature("given a DecidirResponseReason, should map its values to TodoPago equivalents") {

        lateinit var converter: MessageConverter
        lateinit var decidirConverter: DecidirErrorConverter

        beforeEachScenario  {
            converter = mockk()
            decidirConverter = DecidirErrorConverter(converter)
        }

        Scenario("map ids successfully") {
            lateinit var result: GatewayResponse.DecidirResponseReason
            lateinit var reason: GatewayResponse.DecidirResponseReason
            val channel = "25"

            Given("a Decidir response reason") {
                reason = aDecidirResponseReason()
            }

            And("mock default channel"){
                every { converter.defaultChannel } returns channel
            }

            And("mock converter"){
                every { converter.convertDecidirMessageFrom(reason.id.toString(), channel) } returns of(aMessage())
            }

            When("call converter"){
                result = decidirConverter.convert(reason)
            }

            Then("converter has called"){
                verify(exactly = 1) { converter.convertDecidirMessageFrom(reason.id.toString(), channel)}
            }

            And("result is successfully"){
                result.should.be.equal(aDecidirResponseReason(71))
            }

        }

        Scenario("given a DecidirResponseReason, when no error is found, should map return same reason") {
            lateinit var result: GatewayResponse.DecidirResponseReason
            lateinit var reason: GatewayResponse.DecidirResponseReason
            val channel = "25"

            Given("a Decidir response reason") {
                reason = aDecidirResponseReason()
            }

            And("mock default channel"){
                every { converter.defaultChannel } returns channel
            }


            And("mock "){
                every { converter.convertDecidirMessageFrom(reason.id.toString(), channel) } returns empty()
            }

            When("call converter"){
                result = decidirConverter.convert(reason)
            }

            Then("converter has called"){
                verify(exactly = 1) { converter.convertDecidirMessageFrom(reason.id.toString(), channel)}
            }

            And(""){
                result.should.be.equal(reason)
            }

        }
    }

})
