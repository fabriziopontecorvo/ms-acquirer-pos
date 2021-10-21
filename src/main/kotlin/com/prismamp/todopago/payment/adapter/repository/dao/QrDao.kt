package com.prismamp.todopago.payment.adapter.repository.dao

import arrow.core.Either
import arrow.core.Validated.Companion.catch
import arrow.core.computations.either
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.NotFound
import com.prismamp.todopago.util.logs.CompanionLogger
import com.prismamp.todopago.util.logs.benchmark
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class QrDao(
    val jdbcTemplate: NamedParameterJdbcTemplate
) {

    companion object : CompanionLogger() {

        private fun selectQrCodeQueryBy(filters: Map<String, Any>): String =
            "SELECT TOP 1 operation_type " + fromClause + buildWhereClauseByFilters(filters).trimIndent()

        private const val fromClause = " FROM dbo.TransactionsPOS (NOLOCK) "

        private fun buildWhereClauseByFilters(filters: Map<String, Any>): String =
            " WHERE " + filters.keys.joinToString(separator = " AND ") { "$it = :$it" }

    }

    suspend fun findQrOperationBy(filters: Map<String, Any>): Either<ApplicationError, Unit> =
        log.benchmark("findQrOperationBy: search used QR") {
            either {
                catch(f = {
                        jdbcTemplate.queryForObject(
                            selectQrCodeQueryBy(filters),
                            MapSqlParameterSource(filters),
                            String::class.java
                        )
                    }
                ).toEither()
                    .bimap(
                        leftOperation = { NotFound("no se encontro QR") },
                        rightOperation = { }
                    )
                    .bind()
            }
        }


}
