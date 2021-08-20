package com.instantonlinematka.instantonlinematka.model

data class BonusResponse (
    val status: String,
    val response: Boolean,
    val message: String,
    val data: ArrayList<BonusData>
)

data class BonusData (
    val id: String? = null,
    val date: String? = null,
    val time: String? = null,
    val user_id: String? = null,
    val statement_type: String? = null,
    val bid_or_fund_id: String? = null,
    val amount: String? = null,
    val created_date: String? = null,
    val game_type_name: String? = null
)
