package com.instantonlinematka.instantonlinematka.model

data class BiddingHistoryResponse (
    val status: String,
    val response: Boolean,
    val message: String,
    val data: ArrayList<BiddingData>
)

data class BiddingData (
    val game_type_name: String? = null,
    val game_type_id: String? = null,
    val user_id: String? = null
)

data class BiddingHistoryByGame (
    val status: String,
    val response: Boolean,
    val message: String,
    val data: ArrayList<BiddingDataByGame>
)

data class BiddingDataByGame (
    val game_type_name: String? = null,
    val category_name: String? = null,
    val bid_id: String? = null,
    val number: String? = null,
    val type: String? = null,
    val bid_amount: String? = null,
    val bided_for: String? = null,
    val bided_on: String? = null,
    val bided_on_time: String? = null,
    val closing_balance: String? = null,
    val status: String? = null,
    val won_amount: String? = null
)