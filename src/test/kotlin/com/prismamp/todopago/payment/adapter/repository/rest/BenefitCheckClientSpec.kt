package com.prismamp.todopago.payment.adapter.repository.rest

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.configuration.http.RestClient
import com.prismamp.todopago.payment.model.aCheckBenefitRequest
import com.prismamp.todopago.payment.adapter.repository.model.CheckBenefitRequest
import com.prismamp.todopago.payment.domain.model.Benefit
import com.prismamp.todopago.payment.domain.model.BenefitStatus
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.CheckBenefitError
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException

object BenefitCheckClientSpec : Spek({

    Feature("check benefit") {

        lateinit var restClient: RestClient
        lateinit var benefitCheckClient: BenefitCheckClient

        beforeEachScenario {
            restClient = mockk()
            benefitCheckClient = BenefitCheckClient(restClient)
        }

        Scenario("check benefit successfully") {

            lateinit var benefitNumber: BenefitNumber
            lateinit var request: CheckBenefitRequest
            lateinit var result: Either<ApplicationError, Benefit?>
            lateinit var expectedResult: Either<ApplicationError, Benefit?>

            Given("a benefit number") {
                benefitNumber = "1"
                request = aCheckBenefitRequest()
                expectedResult = Right(Benefit(status = BenefitStatus.OK, id = benefitNumber))
            }

            And("mock get") {
                every {
                    restClient.get(
                        url = "".plus("/private/recommendations/$benefitNumber/status/")
                            .plus(request.queryParamsToString()),
                        clazz = Unit::class.java
                    )
                } returns Right(ResponseEntity(Unit, OK))
            }

            When("call check") {
                result = runBlocking { benefitCheckClient.check(benefitNumber, request) }
            }

            Then("get has called") {
                verify(exactly = 1) {
                    restClient.get(
                        url = "".plus("/private/recommendations/$benefitNumber/status/")
                            .plus(request.queryParamsToString()),
                        clazz = Unit::class.java
                    )
                }
            }

            And("result has status ok"){
                result.should.be.equal(expectedResult)
            }

        }

        Scenario("check benefit fail") {

            lateinit var benefitNumber: BenefitNumber
            lateinit var request: CheckBenefitRequest
            lateinit var result: Either<ApplicationError, Benefit?>
            lateinit var expectedResult: Either<ApplicationError, Benefit?>

            Given("a benefit number") {
                benefitNumber = "1"
                request = aCheckBenefitRequest()
                expectedResult = Left(CheckBenefitError(benefitNumber))
            }

            And("mock get") {
                every {
                    restClient.get(
                        url = "".plus("/private/recommendations/$benefitNumber/status/")
                            .plus(request.queryParamsToString()),
                        clazz = Unit::class.java
                    )
                } returns Left(HttpClientErrorException(BAD_REQUEST))
            }

            When("call check") {
                result = runBlocking { benefitCheckClient.check(benefitNumber, request) }
            }

            Then("get has called") {
                verify(exactly = 1) {
                    restClient.get(
                        url = "".plus("/private/recommendations/$benefitNumber/status/")
                            .plus(request.queryParamsToString()),
                        clazz = Unit::class.java
                    )
                }
            }

            And("result has status ok"){
                result.should.be.equal(expectedResult)
            }

        }

    }

})
