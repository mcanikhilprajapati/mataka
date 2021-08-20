package com.instantonlinematka.instantonlinematka.model

data class PaymentStatusResponse (
    val data: ArrayList<DataResponse>? = null
)

data class DataResponse (
    val response_message: String? = null
)