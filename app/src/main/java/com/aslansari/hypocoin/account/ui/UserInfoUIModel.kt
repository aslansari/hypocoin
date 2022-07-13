package com.aslansari.hypocoin.account.ui

import com.aslansari.hypocoin.account.data.UserResult

sealed class UserInfoUIModel {
    object Error: UserInfoUIModel()
    data class User(
        val data: UserResult.User,
    ): UserInfoUIModel()
    object Loading: UserInfoUIModel()
}