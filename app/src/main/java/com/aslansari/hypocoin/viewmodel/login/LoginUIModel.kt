package com.aslansari.hypocoin.viewmodel.login

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

enum class LoginError {
    NONE,
    GENERAL,
    EMAIL_INVALID,
    EMAIL_DOES_NOT_EXISTS,
    PASSWORD_INCORRECT,
    GOOGLE_LOGIN_CANCELED,
    PASSWORD_RESET_EMAIL_NOT_SENT,
    ;
}