package com.instantonlinematka.instantonlinematka.model

data class DepositHistoryResponse(
    val `data`: List<Data>,
    val message: String,
    val response: Boolean,
    val status: String
)


data class Data(
    val amount: String,
    val date: String,
    val status: String,
    val time: String,
    val transaction_id: String
)