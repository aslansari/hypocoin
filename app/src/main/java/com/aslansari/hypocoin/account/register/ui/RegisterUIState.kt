package com.aslansari.hypocoin.account.register.ui

data class RegisterUIState(
    val loading: Boolean = false,
    val error: RegisterStatus = RegisterStatus.NO_ERROR,
    val buttonEnabled: Boolean = true,
    val onSubmit: RegistrationData? = null,
)