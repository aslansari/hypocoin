package com.aslansari.hypocoin.account.login.data

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