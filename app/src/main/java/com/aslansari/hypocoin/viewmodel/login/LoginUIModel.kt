package com.aslansari.hypocoin.viewmodel.login

sealed class LoginUIModel {
    data class Error(val loginError: LoginError): LoginUIModel()
    object Idle: LoginUIModel()
    object Loading: LoginUIModel()
    object Result : LoginUIModel()
}

enum class LoginError {
    NONE,
    GENERAL,
    EMAIL_INVALID,
    EMAIL_DOES_NOT_EXISTS,
    PASSWORD_INCORRECT,
    ;
}