package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.commons.tenant.Tenant
import com.prismamp.todopago.commons.tenant.TenantHolder
import com.prismamp.todopago.commons.tenant.TenantSettings
import com.prismamp.todopago.model.aLimitValidationResponse
import com.prismamp.todopago.model.aValidatableOperation
import com.prismamp.todopago.payment.adapter.repository.kafka.LimitsEventProducer
import com.prismamp.todopago.payment.adapter.repository.model.LimitReport
import com.prismamp.todopago.payment.adapter.repository.model.LimitValidationRequest
import com.prismamp.todopago.payment.adapter.repository.rest.LimitsClient
import com.prismamp.todopago.payment.application.usecase.ValidatableOperation
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.LimitValidationError
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object LimitAdapterSpec : Spek({

    lateinit var limitsClient: LimitsClient
    lateinit var limitsEventProducer: LimitsEventProducer
    lateinit var tenantHolder: TenantHolder
    lateinit var tenantSettings: TenantSettings
    lateinit var limitAdapter: LimitAdapter

    Feature("validate Limit") {

        beforeEachScenario {
            limitsClient = mockk()
            limitsEventProducer = mockk()
            tenantHolder = mockk()
            tenantSettings = mockk()
            limitAdapter = LimitAdapter(limitsClient, limitsEventProducer, tenantHolder, tenantSettings)
        }

        Scenario("validate limit successfully") {

            lateinit var validatableOperation: ValidatableOperation
            lateinit var result: Either<ApplicationError, Unit>

            Given("a validatable operation") {
                validatableOperation = aValidatableOperation()
            }

            And("mock validation") {
                every {
                    runBlocking {
                        limitsClient.validation(
                            request = LimitValidationRequest.from(validatableOperation),
                            accountId = validatableOperation.second.id
                        )
                    }
                } returns Right(aLimitValidationResponse())
            }

            And("mock feature flag") {
                every {
                    tenantSettings.featureIsEnabled("limit-validation")
                } returns true
            }

            When("call validate limit") {
                with(limitAdapter) {
                    result = runBlocking { validatableOperation.validateLimit() }
                }
            }

            Then("validation was called") {
                verify(exactly = 1) {
                    runBlocking {
                        limitsClient.validation(
                            request = LimitValidationRequest.from(validatableOperation),
                            accountId = validatableOperation.second.id
                        )
                    }
                }
            }

            And("successfully response") {
                result.should.be.equal(Right(Unit))
            }

        }

        Scenario("validate limit - feature disabled") {

            lateinit var validatableOperation: ValidatableOperation
            lateinit var result: Either<ApplicationError, Unit>

            Given("a validatable operation") {
                validatableOperation = aValidatableOperation()
            }


            And("mock feature flag") {
                every {
                    tenantSettings.featureIsEnabled("limit-validation")
                } returns false
            }

            When("call validate limit") {
                with(limitAdapter) {
                    result = runBlocking { validatableOperation.validateLimit() }
                }
            }

            Then("validation not was called") {
                verify(exactly = 0) {
                    runBlocking {
                        limitsClient.validation(
                            request = LimitValidationRequest.from(validatableOperation),
                            accountId = validatableOperation.second.id
                        )
                    }
                }
            }

            And("successfully response") {
                result.should.be.equal(Right(Unit))
            }

        }

        Scenario("validate limit failed with rejections") {

            lateinit var validatableOperation: ValidatableOperation
            lateinit var result: Either<ApplicationError, Unit>

            Given("a validatable operation") {
                validatableOperation = aValidatableOperation()
            }

            And("mock validation") {
                every {
                    runBlocking {
                        limitsClient.validation(
                            request = LimitValidationRequest.from(validatableOperation),
                            accountId = validatableOperation.second.id
                        )
                    }
                } returns Right(
                    aLimitValidationResponse()
                        .copy(
                            status = "REJECTED",
                            rejections = listOf(
                                LimitReport(null, null, null)
                            )
                        )
                )
            }

            And("mock feature flag") {
                every {
                    tenantSettings.featureIsEnabled("limit-validation")
                } returns true
            }

            And("mock tenant holder") {
                every {
                    tenantHolder.getCurrent()
                } returns Tenant.Known("bimo")
            }

            And("mock producer"){
                every {
                    limitsEventProducer.produce(any())
                } returns Unit
            }

            When("call validate limit") {
                with(limitAdapter) {
                    result = runBlocking { validatableOperation.validateLimit() }
                }
            }

            Then("validation was called") {
                verify(exactly = 1) {
                    runBlocking {
                        limitsClient.validation(
                            request = LimitValidationRequest.from(validatableOperation),
                            accountId = validatableOperation.second.id
                        )
                    }
                }
            }

            And("produce was called"){
                verify(exactly = 1) {
                    limitsEventProducer.produce(any())
                }
            }

            And("successfully response") {
                result.should.be.equal(Left( LimitValidationError( "'empty field'")))
            }

        }

        Scenario("validate limit successfully with warning") {

            lateinit var validatableOperation: ValidatableOperation
            lateinit var result: Either<ApplicationError, Unit>

            Given("a validatable operation") {
                validatableOperation = aValidatableOperation()
            }

            And("mock validation") {
                every {
                    runBlocking {
                        limitsClient.validation(
                            request = LimitValidationRequest.from(validatableOperation),
                            accountId = validatableOperation.second.id
                        )
                    }
                } returns Right(
                    aLimitValidationResponse()
                        .copy(
                            status = "WARNING",
                            warnings = listOf(
                                LimitReport(null, null, null)
                            )
                        )
                )
            }

            And("mock feature flag") {
                every {
                    tenantSettings.featureIsEnabled("limit-validation")
                } returns true
            }

            And("mock tenant holder") {
                every {
                    tenantHolder.getCurrent()
                } returns Tenant.Known("bimo")
            }

            And("mock producer"){
                every {
                    limitsEventProducer.produce(any())
                } returns Unit
            }

            When("call validate limit") {
                with(limitAdapter) {
                    result = runBlocking { validatableOperation.validateLimit() }
                }
            }

            Then("validation was called") {
                verify(exactly = 1) {
                    runBlocking {
                        limitsClient.validation(
                            request = LimitValidationRequest.from(validatableOperation),
                            accountId = validatableOperation.second.id
                        )
                    }
                }
            }

            And("produce was called"){
                verify(exactly = 1) {
                    limitsEventProducer.produce(any())
                }
            }

            And("successfully response") {
                result.should.be.equal(Right(Unit))
            }

        }

    }

})
