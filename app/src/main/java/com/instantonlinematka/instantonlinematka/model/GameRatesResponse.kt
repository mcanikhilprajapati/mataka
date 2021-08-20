package com.instantonlinematka.instantonlinematka.model

data class GameRatesResponse (
    val status: Int,
    val response: Boolean,
    val message: String,
    val data: ArrayList<RatesData>
)

data class RatesData (
    val id: String? = null,
    val is_starline: String? = null,
    val game_cat_id: String? = null,
    val winning_ratio: String? = null,
    val created_date: String? = null,
    val updated_date: String? = null,
    val category_name: String? = null,
)
