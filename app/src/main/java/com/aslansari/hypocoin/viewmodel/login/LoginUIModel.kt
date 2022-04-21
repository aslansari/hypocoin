package com.aslansari.hypocoin.viewmodel.login

sealed class LoginUIModel {
    object Fail: LoginUIModel()
    object Complete: LoginUIModel()
}