package com.instantonlinematka.instantonlinematka.model

data class GameCategoryResponse (
    val status: String,
    val response: Boolean,
    val message: String,
    val data: ArrayList<GameCategoryData>
)

data class GameCategoryData (
    val id: String? = null,
    val game_id: String? = null,
    val category_id: String? = null,
    val category_name: String? = null,
)