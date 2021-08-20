package com.instantonlinematka.instantonlinematka.model

data class GameDataResponse(
    val status: String,
    val response: Boolean,
    val message: String,
    val data: GameData
)

data class GameData(
    val game_type_name: String? = null,
    val open_time: String? = null,
    val close_time: String? = null,
    val game_date: String? = null,
    val game_type_id: String? = null,
    val game_status: String? = null,
    val oldgameresult: String? = null,
    val time: String? = null,
    val date: String? = null
)