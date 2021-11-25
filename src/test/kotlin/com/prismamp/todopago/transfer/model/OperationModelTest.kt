package com.prismamp.todopago.transfer.model

import com.prismamp.todopago.enum.Concept.VAR
import com.prismamp.todopago.enum.Currency.ARS
import com.prismamp.todopago.transfer.domain.model.Operation

fun anOperation() =
    Operation(
        seller = Operation.Seller(
            cuit = "27174999495",
            cbu = "00000000068000000002222956",
            bank = "000",
            branch = "0000",
            terminal = "23999976"
        ),
        buyer = Operation.Buyer(
            cbu = "00000000068000000001111234",
            cuit = "20376671292"
        ),
        detail = Operation.QrDetail(
            concept = VAR,
            currency = ARS,
            amount = "100.0",
            expirationTime = "1",
            description = "description",
            qr = "0002010102125017001327-17499949-551260022000006800000000222295652045812530303254033.15802AR5911TEST NESTOR6012VILLA GESELL610507165627001139999-000001230708239999765003APS51031.0520105312201116154348540230802123999976160554142851784020181803wOn+/AuHaSdi2QHMAx5SwUFSWcH1FrIATiSm/kihctPHIRxDamoHsWlWNYfBjmY9RKzxfsmiVznAM+G8280ebTynH0H9Oq5txD3NErEpFOYoTVB9UIISeRJybDfbqe4eOtlcykUdlICk687wy+tYFdUEIIGZMU9HKlK8312Ea9j1FBWt4I=63043edd",
            qrTransactionId = "1",
            walletId = "1"
        )

    )
