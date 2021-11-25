package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.payment.model.anAccount
import com.prismamp.todopago.payment.model.anOperation
import com.prismamp.todopago.payment.adapter.repository.rest.AccountClient
import com.prismamp.todopago.payment.domain.model.Account
import com.prismamp.todopago.payment.domain.model.Operation
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.InvalidAccount
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object AccountAdapterSpec: Spek({

    Feature("get account") {
        lateinit var accountClient: AccountClient
        lateinit var accountAdapter: AccountAdapter
        val accountId = "1"

        beforeEachScenario {
            accountClient = mockk()
            accountAdapter = AccountAdapter(accountClient)
        }

        Scenario("obtain an account"){

            lateinit var result: Either<ApplicationError, Account>
            lateinit var operation: Operation
            lateinit var account: Account

            Given("a operation"){

                operation = anOperation()
                account = anAccount()

                every {
                   runBlocking { accountClient.getAccountBy(accountId) }
                } returns Right(account)
            }

            When("call get account"){
                with(accountAdapter){
                    result = runBlocking { operation.getAccount() }
                }
            }

            Then("return a account successfully"){
                result.should.be.equal(Right(anAccount()))
            }

        }

        Scenario("fail when try to get a account"){

            lateinit var result: Either<ApplicationError, Account>

            lateinit var operation: Operation

            Given("a operation"){

                operation = anOperation()

                every {
                    runBlocking { accountClient.getAccountBy(accountId) }
                } returns Left(InvalidAccount(accountId))
            }

            When("call get account"){
                with(accountAdapter){
                    result = runBlocking { operation.getAccount() }
                }
            }

            Then("return a account fail"){
                result.should.be.equal(Left(InvalidAccount(accountId)))
            }

        }

    }
})
