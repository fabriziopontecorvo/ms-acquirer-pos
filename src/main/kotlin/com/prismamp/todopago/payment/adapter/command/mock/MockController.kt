package com.prismamp.todopago.payment.adapter.command.mock

import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@Profile("dev", "qa")
@RestController
class MockController {

    @PostMapping("/transactor/payments/qr", produces = ["application/json"])
    fun mock() =
        ResponseEntity(
            """{
                    "id": 145,
                    "qr_id":"QR 1234568",
                    "establishment_id":"29880765",
                    "transaction_datetime":"2019-10-28T11:47:43",
                    "transaction_id":"1234567",
                    "payment_method_id":1,
                    "card_data":{
                        "card_brand":"Visa",
                        "bin":"450799",
                        "last_four_digits":"4851",
                        "bank_data":{
                            "id":1,
                            "description":"Banco Galicia"
                        }
                    },
                    "amount":50000,
                    "currency":"ARS",
                    "installments":1,
                    "status":"pending",
                    "status_details":{
                        "card_authorization_code":"",
                        "card_reference_number":"",
                            "response":{
                            "type":"no_response",
                            "reason":{
                                "id": 0,
                                "description":"",
                                "additional_description":""
                            }
                        }
                    },
                    "terminal_data":{
                        "trace_number":"1090",
                        "ticket_number":"129",
                        "terminal_number":"12341234"
                    }
                }
                """, HttpStatus.OK
        )

}
