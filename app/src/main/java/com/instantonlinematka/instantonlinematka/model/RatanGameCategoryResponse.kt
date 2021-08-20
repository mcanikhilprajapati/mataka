package com.instantonlinematka.instantonlinematka.model

class RatanGameCategoryResponse(
    val status: String,
    val response: Boolean,
    val message: String,
    val data: ArrayList<RatanGameCategoryData>
)

data class RatanGameCategoryData(
    val cat_id: String? = null,
    val category_name: String? = null,
)