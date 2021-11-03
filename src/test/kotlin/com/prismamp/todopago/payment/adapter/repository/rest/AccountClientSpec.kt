package com.prismamp.todopago.payment.adapter.repository.rest

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.configuration.Constants.Companion.APP_NAME
import com.prismamp.todopago.configuration.Constants.Companion.MS_ACCOUNT
import com.prismamp.todopago.configuration.http.RestClient
import com.prismamp.todopago.model.anAccount
import com.prismamp.todopago.model.anAccountResponse
import com.prismamp.todopago.payment.adapter.repository.model.AccountResponse
import com.prismamp.todopago.payment.domain.model.Account
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.InvalidAccount
import com.prismamp.todopago.util.ServiceCommunication
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException

object AccountClientSpec : Spek({

    Feature("get account by id") {

        lateinit var restClient: RestClient
        lateinit var accountClient: AccountClient
        lateinit var accountId: String
        lateinit var url: String

        beforeEachScenario {
            accountId = "1"
            url = ""
            restClient = mockk()
            accountClient = AccountClient(restClient)
        }

        Scenario("get successfully account") {
            lateinit var accountResponse: ResponseEntity<AccountResponse>
            lateinit var result: Either<ApplicationError, Account>
            lateinit var expectedResult: Either<ApplicationError, Account>


            Given("mock account response") {
                accountResponse = ResponseEntity(anAccountResponse(), HttpStatus.OK)
                expectedResult = Right(anAccount())
            }

            And("mock rest client") {
                every {
                    restClient.get(
                        url = "$url/v3/accounts/$accountId",
                        clazz = AccountResponse::class.java
                    )
                } returns Right(accountResponse)
            }

            When("call get account") {
                result = runBlocking { accountClient.getAccountBy(accountId) }
            }

            Then("get account has called") {
                verify(exactly = 1) {
                    restClient.get(
                        url = "$url/v3/accounts/$accountId",
                        clazz = AccountResponse::class.java
                    )
                }
            }

            And(""){
                result.should.be.equal(expectedResult)
            }

        }

        Scenario("Not found account") {
            lateinit var result: Either<ApplicationError, Account>
            lateinit var expectedResult: Either<ApplicationError, Account>

            Given("mock account response") {
                expectedResult = Left(InvalidAccount(accountId))
            }

            And("mock rest client") {
                every {
                    restClient.get(
                        url = "$url/v3/accounts/$accountId",
                        clazz = AccountResponse::class.java
                    )
                } returns Left(HttpClientErrorException(NOT_FOUND))
            }

            When("call get account") {
                result = runBlocking { accountClient.getAccountBy(accountId) }
            }

            Then("get account has called") {
                verify(exactly = 1) {
                    restClient.get(
                        url = "$url/v3/accounts/$accountId",
                        clazz = AccountResponse::class.java
                    )
                }
            }

            And("expect a not found"){
                result.should.be.equal(expectedResult)
            }

        }

        Scenario("service comunnication error") {
            lateinit var result: Either<ApplicationError, Account>
            lateinit var expectedResult: Either<ApplicationError, Account>

            Given("mock account response") {
                expectedResult = Left(ServiceCommunication(APP_NAME, MS_ACCOUNT))
            }

            And("mock rest client") {
                every {
                    restClient.get(
                        url = "$url/v3/accounts/$accountId",
                        clazz = AccountResponse::class.java
                    )
                } returns Left(HttpClientErrorException(INTERNAL_SERVER_ERROR))
            }

            When("call get account") {
                result = runBlocking { accountClient.getAccountBy(accountId) }
            }

            Then("get account has called") {
                verify(exactly = 1) {
                    restClient.get(
                        url = "$url/v3/accounts/$accountId",
                        clazz = AccountResponse::class.java
                    )
                }
            }

            And("expect a not found"){
                result.should.be.equal(expectedResult)
            }

        }

    }

})
