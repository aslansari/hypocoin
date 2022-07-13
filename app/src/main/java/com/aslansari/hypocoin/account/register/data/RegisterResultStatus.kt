package com.aslansari.hypocoin.account.register.data

enum class RegisterResultStatus {
    SUCCESS,
    NO_ERROR,
    NOT_VALID,
    DOES_NOT_MATCH,
    SHOULD_NOT_BE_EMPTY,
    CONFIRM_YOUR_PASSWORD,
    WEAK_PASSWORD,
    INVALID_CREDENTIALS,
    USER_ALREADY_EXISTS,
    REGISTER_ERROR,
}