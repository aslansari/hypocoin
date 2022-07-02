package com.aslansari.hypocoin.repository

import com.aslansari.hypocoin.viewmodel.login.LoginError

data class LoginResult(
    val isSuccess: Boolean,
    val isCanceled: Boolean,
    val loginError: LoginError = LoginError.GENERAL
)
