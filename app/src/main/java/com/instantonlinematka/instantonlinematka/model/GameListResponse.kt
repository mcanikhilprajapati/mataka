package com.instantonlinematka.instantonlinematka.model

data class GameListResponse(
    val response: String,
    val message: String,
    val data: ArrayList<GameListData>
)

data class GameListData (
    val game_id: String? = null,
    val game_type_name: String? = null,
    val open_time: String? = null,
    val close_time: String? = null,
    val game_type_id: String? = null,
    val open_result: String? = null,
    val close_result: String? = null,
    val center_open_result: String? = null,
    val center_close_result: String? = null,
    val game_status: String? = null,
    val isDisabled: String? = null
)