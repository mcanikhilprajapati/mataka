package com.instantonlinematka.instantonlinematka.model

data class RatanStarlineGameResponse (
    val status: String,
    val response: Boolean,
    val message: String,
    val data: ArrayList<RatanStarlineGameData>
)

data class RatanStarlineGameData (
    val game_id: String? = null,
    val game_date: String? = null,
    val open_time: String? = null,
    val close_time: String? = null,
    val is_disabled: String? = null,
    val game_status: String? = null,
    val panna_result: String? = null,
    val single_digit_result: String? = null,
)
