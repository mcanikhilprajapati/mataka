package com.instantonlinematka.instantonlinematka.model

data class AddFundsResponse(
    val status: String,
    val user: String,
    val response: Boolean,
    val message: String,
    val data: String,
    val transaction_id: String,
    val payment: PaymentResponse
)

class PaymentResponse (
    val user: String,
    val vpa: String
)