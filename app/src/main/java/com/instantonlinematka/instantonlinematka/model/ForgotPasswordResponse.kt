package com.instantonlinematka.instantonlinematka.model

data class ForgotPasswordResponse (
    val status: Int,
    val response: Boolean,
    val message: String,
    val data: ArrayList<ForgotPasswordData>
)

class ForgotPasswordData {

}