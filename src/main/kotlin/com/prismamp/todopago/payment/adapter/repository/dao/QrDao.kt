package com.prismamp.todopago.payment.adapter.repository.dao

import arrow.core.Option
import arrow.core.computations.option
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

    suspend fun findQrOperationBy(filters: Map<String, Any>): Option<String> =
        log.benchmark("findQrOperationBy: search used QR") {
            option {
                jdbcTemplate.queryForObject(
                    selectQrCodeQueryBy(filters),
                    MapSqlParameterSource(filters),
                    String::class.java
                ).bind()
            }
        }




}
