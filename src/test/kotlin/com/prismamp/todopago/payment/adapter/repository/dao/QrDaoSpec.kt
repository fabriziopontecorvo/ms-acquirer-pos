package com.prismamp.todopago.payment.adapter.repository.dao

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.prismamp.todopago.model.aFiltersMap
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.NotFound
import com.winterbe.expekt.should
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource

object QrDaoSpec : Spek({

    Feature("search qr") {

        lateinit var jdbcTemplate: NamedParameterJdbcTemplate
        lateinit var qrDao: QrDao

        beforeEachScenario {
            jdbcTemplate = mockk()
            qrDao = QrDao(jdbcTemplate)
        }

        Scenario("find used Qr") {

            lateinit var filters: Map<String, Any>
            lateinit var result: Either<ApplicationError, Unit>

            Given("a filters map") {
                filters = aFiltersMap()
            }

            And("mock template") {
                every {
                    jdbcTemplate.queryForObject(any() as String, any() as SqlParameterSource, any() as Class<*>)
                } returns "COMPRA_QR_ADQ"
            }

            When("execute query") {
                result = runBlocking { qrDao.findQrOperationBy(filters) }
            }

            Then("template has called") {
                verify(exactly = 1) {
                    jdbcTemplate.queryForObject(
                        any() as String,
                        any() as SqlParameterSource,
                        any() as Class<*>
                    )
                }
            }

            And("returns unit"){
                result.should.be.equal(Right(Unit))
            }
        }

        Scenario("not find used Qr") {

            lateinit var filters: Map<String, Any>
            lateinit var result: Either<ApplicationError, Unit>

            Given("a filters map") {
                filters = aFiltersMap()
            }

            And("mock template") {
                every {
                    jdbcTemplate.queryForObject(any() as String, any() as SqlParameterSource, any() as Class<*>)
                } throws DataAccessResourceFailureException("")
            }

            When("execute query") {
                result = runBlocking { qrDao.findQrOperationBy(filters) }
            }

            Then("template has called") {
                verify(exactly = 1) {
                    jdbcTemplate.queryForObject(
                        any() as String,
                        any() as SqlParameterSource,
                        any() as Class<*>
                    )
                }
            }

            And("returns Not Found Qr"){
                result.should.be.equal(Left(NotFound("no se encontro QR")))
            }
        }


    }

})
