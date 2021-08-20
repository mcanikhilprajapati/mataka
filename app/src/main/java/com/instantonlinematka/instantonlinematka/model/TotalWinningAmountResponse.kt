package com.instantonlinematka.instantonlinematka.model

class TotalWinningAmountResponse (

    val response: Boolean,
    val message: String,
    val data: WinningData
)

data class WinningData (
    val total: Int,
    val graph: ArrayList<GraphData>
)

data class GraphData (
    val total: Int? = null,
    val date: String? = null
)