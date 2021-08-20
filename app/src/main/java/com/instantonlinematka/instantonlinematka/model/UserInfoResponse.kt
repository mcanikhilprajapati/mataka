package com.instantonlinematka.instantonlinematka.model

data class UserInfoResponse(
    val status: Int,
    val response: Boolean,
    val message: String,
    val user: UserInfoData
)

data class UserInfoData(
    val id: String? = null,
    val name: String? = null,
    val ful_name: String? = null,
    val profile_pic: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val wallet: String? = null,
    val status: String? = null,
    val password: String? = null,
    val bank_name: String? = null,
    val bankholder_name: String? = null,
    val Ifc_code: String? = null,
    val account_no: String? = null,
    val referal: String? = null,
    val use_referal: String? = null,
    val otp: String? = null,
    val logged: String? = null,
    val gender: String? = null,
    val dob: String? = null,
    val address: String? = null,
    val google_pay: String? = null,
    val paytm: String? = null,
    val phone_pay: String? = null,
    val notification_count: String? = null,
    val fcm_token: String? = null,
)