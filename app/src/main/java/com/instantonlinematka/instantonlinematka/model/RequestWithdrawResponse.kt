package com.instantonlinematka.instantonlinematka.model

data class RequestWithdrawResponse (
    val user: Boolean,
    val status: String,
    val response: Boolean,
    val message: String,
)
