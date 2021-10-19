package com.prismamp.todopago.payment.adapter

import arrow.core.Either
import arrow.core.Either.Companion.conditionally
import arrow.core.computations.either
import arrow.core.flatMap
import arrow.core.handleErrorWith
import com.prismamp.todopago.commons.tenant.TenantHolder
import com.prismamp.todopago.commons.tenant.TenantSettings
import com.prismamp.todopago.configuration.Constants
import com.prismamp.todopago.configuration.Constants.Companion.BIMO_TENANT
import com.prismamp.todopago.configuration.Constants.Companion.TP_TENANT
import com.prismamp.todopago.enum.LimitActionType
import com.prismamp.todopago.enum.LimitActionType.REJECTED
import com.prismamp.todopago.enum.LimitActionType.WARNING
import com.prismamp.todopago.enum.LimitNotificationTemplate
import com.prismamp.todopago.enum.LimitNotificationTemplate.*
import com.prismamp.todopago.enum.LimitType
import com.prismamp.todopago.enum.LimitType.RISK_LIMIT
import com.prismamp.todopago.enum.LimitType.TP_LIMIT
import com.prismamp.todopago.payment.adapter.repository.kafka.LimitsEventProducer
import com.prismamp.todopago.payment.adapter.repository.model.*
import com.prismamp.todopago.payment.adapter.repository.rest.LimitsClient
import com.prismamp.todopago.payment.application.port.out.LimitOutputPort
import com.prismamp.todopago.payment.application.usecase.ValidatablePayment
import com.prismamp.todopago.util.ApplicationError
import com.prismamp.todopago.util.LimitValidationError
import com.prismamp.todopago.util.logs.CompanionLogger
import com.prismamp.todopago.util.tenant.FeatureToggleComponent
import kotlinx.coroutines.CoroutineScope
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class LimitAdapter(
    private val limitsClient: LimitsClient,
    private val limitsEventProducer: LimitsEventProducer,
    tenantHolder: TenantHolder,
    tenantSettings: TenantSettings
) : FeatureToggleComponent(tenantSettings, tenantHolder), LimitOutputPort {

    companion object : CompanionLogger() {
        private const val FEATURE_LIMIT_ACCUMULATION_ENABLED = "limit-validation"
    }

    override suspend fun ValidatablePayment.validateLimit(): Either<ApplicationError, Unit> =
        let { payment ->
            executeFeatureOrDefault(
                feature = FEATURE_LIMIT_ACCUMULATION_ENABLED,
                default = Either.Right(Unit)
            ) {
                limitsClient.validation(
                    request = LimitValidationRequest.from(payment),
                    accountId = second.id
                )
                    .map { it.toLimitResult() }
                    .map { it.handleLimitNotSatisfiedEvent(payment) }
                    .map { it.handleWarnings() }
                    .flatMap { it.handleRejections() }
            }
        }

    private fun LimitValidationResponse.toLimitResult() =
        LimitValidationResult(
            limitReport = getLimitReport(warnings ?: emptyList(), rejections ?: emptyList()),
            dailyAmount = dailyAmount ?: BigDecimal.valueOf(0),
            thirtyDaysAmount = thirtyDaysAmount ?: BigDecimal.valueOf(0),
            dailyTransactions = dailyTransactions ?: 0,
            thirtyDaysTransactions = thirtyDaysTransactions ?: 0,
            status = status
        ).log { info("Limit Result: {}", it.status) }

    private fun getLimitReport(warnings: List<LimitReport>, rejections: List<LimitReport>): LimitReport? =
        rejections
            .takeIf { it.isNotEmpty() }
            ?.let { rejections[0] }
            ?: warnings
                .takeIf { it.isNotEmpty() }
                ?.let { warnings[0] }

    private fun LimitValidationResult.handleLimitNotSatisfiedEvent(validatablePayment: ValidatablePayment) =
        also {
            if (shouldNotify()) {
                limitsEventProducer.produce(
                    NotSatisfiedLimitEvent(
                        getLimitNotificationId(limitReport?.limitType, status),
                        validatablePayment.first.amount,
                        validatablePayment.second.identification,
                        validatablePayment.second.id,
                        validatablePayment.third.bank.id,
                        validatablePayment.first.establishmentInformation.sellerName,
                        dailyAmount,
                        thirtyDaysAmount,
                        dailyTransactions,
                        thirtyDaysTransactions
                    )
                ).log { info("handleLimitNotSatisfiedEvent: Se ejecuto evento de limito no satistecho") }
            }
        }


    private fun getLimitNotificationId(limitType: String?, status: String): Long =
        getNotificationTemplate(limitType, status)
            .let {
                when (getCurrentTenant()) {
                    BIMO_TENANT -> it.notificationBimoId
                    TP_TENANT -> it.notificationId
                    else -> it.notificationId
                }
            }


    private fun getNotificationTemplate(limitType: String?, limitAction: String): LimitNotificationTemplate {
        return when {
            REJECTED.description == limitAction && limitType == RISK_LIMIT.description -> RISK_REJECT
            WARNING.description == limitAction && limitType == RISK_LIMIT.description -> RISK_WARNING
            REJECTED.description == limitAction && limitType == TP_LIMIT.description -> TP_REJECT
            WARNING.description == limitAction && limitType == TP_LIMIT.description -> TP_WARNING
            else -> INVALID
        }
    }

    private fun LimitValidationResult.shouldNotify() = status == WARNING.description || status == REJECTED.description

    private fun LimitValidationResult.handleWarnings() =
        also {
            takeIf { it.status == WARNING.description }
                ?.let { log.info("resolveLimitValidation: Limite de alerta superado, se envia notificacion: {}", it) }
        }

    private fun LimitValidationResult.handleRejections(): Either<ApplicationError, Unit> =
        conditionally(
            test = status != REJECTED.description,
            ifFalse = { LimitValidationError(limitReport?.overpastField ?: "'empty field'") },
            ifTrue = { }
        )

}
