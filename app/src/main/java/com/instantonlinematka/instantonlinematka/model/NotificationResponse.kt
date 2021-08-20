package com.instantonlinematka.instantonlinematka.model

data class NotificationResponse(
    val status: String,
    val response: Boolean,
    val message: String,
    val data: ArrayList<NotificationData>
)

data class NotificationData (
    val nid: String? = null,
    val user_id: String? = null,
    val title: String? = null,
    val created_date: String? = null,
    val type: String? = null,
    val amount: String? = null,
    val msg: String? = null,
)