package com.instantonlinematka.instantonlinematka.model

data class BidRequestResponse (
    val status: String,
    val response: Boolean,
    val data: String,
    val walletamount: String
)