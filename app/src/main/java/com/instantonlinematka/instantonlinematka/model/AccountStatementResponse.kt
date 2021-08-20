package com.instantonlinematka.instantonlinematka.model

data class AccountStatementResponse(

    val status: String,
    val response: Boolean,
    val message: String,
    val data: ArrayList<AccountStatementData>
)

data class AccountStatementData (
    val date: String? = null,
    val time: String? = null,
    val amount: String? = null,
    val bid_or_fund_id: String? = null,
    val statement_type: String? = null,
    val biddata: BidData? = null
)

data class BidData(
    val game_type_name: String? = null,
    val is_starline: String? = null,
    val win_status: String? = null,
    val category_name: String? = null,
    val bid_amount: String? = null,
    val number: String? = null,
    val open_panna: String? = null,
    val close_panna: String? = null,
    val open_digit: String? = null,
    val close_digit: String? = null
)