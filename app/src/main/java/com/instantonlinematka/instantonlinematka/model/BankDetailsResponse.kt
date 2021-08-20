package com.instantonlinematka.instantonlinematka.model

data class BankDetailsResponse (
    val status: String,
    val message: String,
    val response: Boolean,
)

data class CheckStatusResponse (
    val data: StatusData? = null
)

data class StatusData(
    val status: String,
    val beneficiary_name: String
)
