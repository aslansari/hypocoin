package com.aslansari.hypocoin.register.dto

data class RegisterInput(
    val username: String,
    val password: String,
    val passwordRepeat: String
)