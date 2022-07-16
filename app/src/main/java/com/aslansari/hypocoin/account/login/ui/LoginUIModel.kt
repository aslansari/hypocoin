package com.aslansari.hypocoin.account.login.ui

import com.aslansari.hypocoin.account.login.data.LoginError
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

sealed class LoginUIModel {
    data class Error(val loginError: LoginError): LoginUIModel()
    object Idle: LoginUIModel()
    object Loading: LoginUIModel()
    data class Result(val loginResult: LoginResult) : LoginUIModel()
    data class RegisterWithGoogle(val account: GoogleSignInAccount): LoginUIModel()
}

enum class LoginResult {
    GOOGLE_LOGIN_REQUEST,
    EMAIL_VALID,
    LOGIN_SUCCESS,
    PASSWORD_RESET_EMAIL_SENT,
}
