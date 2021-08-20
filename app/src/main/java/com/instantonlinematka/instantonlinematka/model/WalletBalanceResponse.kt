package com.instantonlinematka.instantonlinematka.model

data class WalletBalanceResponse(
    val status: String,
    val response: Boolean,
    val message: String,
    val user: UserWallet,
    val payment: ArrayList<UserPayment>
)

data class UserWallet (
    val wallet: String
)

data class UserPayment(
    val id: Int,
    val name: String,
    val code: String,
    val status: Int
)