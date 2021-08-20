package com.instantonlinematka.instantonlinematka.model

data class RatanStarlineBidHistoryResponse(
    val status: String,
    val response: Boolean,
    val message: String,
    val data: ArrayList<RatanBidHistoryData>
)

data class RatanBidHistoryData(
    val game_id: String? = null,
    val category_name: String? = null,
    val bid_id: String? = null,
    val panna_result: String? = null,
    val single_digit_result: String? = null,
    val bid_amount: String? = null,
    val bided_for: String? = null,
    val bided_for_time: String? = null,
    val bided_on: String? = null,
    val bided_on_time: String? = null,
    val closing_balance: String? = null,
    val status: String? = null,
    val won_amount: String? = null
)


