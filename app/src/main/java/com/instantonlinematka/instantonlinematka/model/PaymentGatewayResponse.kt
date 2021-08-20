package com.instantonlinematka.instantonlinematka.model

data class PaymentGatewayResponse (
    val data: PaymentGatewayData? = null
)

data class PaymentGatewayData (
    val url: String? = null,
    val uuid: String? = null,
    val expiry_datetime: String? = null,
    val order_id: String? = null,
    val vpa: String? = null
)