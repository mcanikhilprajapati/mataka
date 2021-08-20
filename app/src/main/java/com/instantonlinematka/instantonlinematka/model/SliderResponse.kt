package com.instantonlinematka.instantonlinematka.model

data class SliderResponse (
    val status: String,
    val responce: Boolean,
    val message: String,
    val version_code: String,
    val data: ArrayList<SliderData>
)

data class SliderData (
    val slider_id: String,
    val slider_name: String,
    val slider_image: String,
    val status: String
)