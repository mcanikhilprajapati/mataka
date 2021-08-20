package com.instantonlinematka.instantonlinematka.model

data class GameDataList(
    val numbers: String,
    val points: Int
)

data class PannaGameDataList (
    val numbers: String,
    val points: Int,
    val digit: Int,
)

data class PannaList (
    val digits: String,
    val pannas: ArrayList<String>
)

data class SangamGameData (
    val numbers: String,
    val points: String
)

data class FullSangamGameData (
    val digits: String,
    val value: String
)