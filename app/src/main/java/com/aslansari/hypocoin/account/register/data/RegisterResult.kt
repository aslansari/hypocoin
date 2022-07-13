package com.aslansari.hypocoin.account.register.data

data class RegisterResult(
    val errorCode: Int,
    val errorMessage: String,
    val status: RegisterResultStatus,
)