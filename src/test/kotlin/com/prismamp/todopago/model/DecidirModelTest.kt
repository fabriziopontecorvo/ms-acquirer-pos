import com.prismamp.todopago.Message
import com.prismamp.todopago.payment.domain.model.GatewayResponse

fun aDecidirResponseReason(id: Int = 1) =
    GatewayResponse.DecidirResponseReason(
        id = id,
        description = "Error al realizar la compra",
        additionalDescription = ""
    )

fun aMessage() =
    Message(
        id = "1",
        code = "071",
        text = "Error al realizar la compra"
    )
