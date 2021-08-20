package com.instantonlinematka.instantonlinematka.model

data class PreviousGameInfoResponse (

    val status: String,
    val response: Boolean,
    val message: String,
    val data: PreviousGameData
)

data class PreviousGameData(
    val game_type_name: String? = null,
    val open_time: String? = null,
    val close_time: String? = null,
    val time: String? = null,
    val game_date: String? = null,
    val game_type_id: String? = null,
    val game_status: String? = null,
    val oldgameresult: String? = null,
)