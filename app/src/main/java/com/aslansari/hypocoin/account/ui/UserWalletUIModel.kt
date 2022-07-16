package com.aslansari.hypocoin.account.ui

import com.aslansari.hypocoin.account.data.UserResult

sealed class UserWalletUIModel {
    object Error: UserWalletUIModel()
    data class Result(
        val user: UserResult.User,
        val assets: List<AssetListItem>,
        val netWorthUIModel: NetWorthUIModel,
    ): UserWalletUIModel()
    object NotLogin: UserWalletUIModel()
    object Loading: UserWalletUIModel()
}