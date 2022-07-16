package com.aslansari.hypocoin.account.register.ui

import com.aslansari.hypocoin.account.register.data.RegisterResultStatus

data class RegisterResultUIState(
    val loading: Boolean = false,
    val error: RegisterResultStatus = RegisterResultStatus.NO_ERROR,
    val buttonEnabled: Boolean = true,
)