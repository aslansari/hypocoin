package com.aslansari.hypocoin.account.data

import com.aslansari.hypocoin.account.login.data.LoginError

data class LoginResult(
    val isSuccess: Boolean,
    val isCanceled: Boolean,
    val loginError: LoginError = LoginError.GENERAL
)
