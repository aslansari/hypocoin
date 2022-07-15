package com.aslansari.hypocoin.account.domain

import com.aslansari.hypocoin.account.data.AccountRepository
import com.aslansari.hypocoin.account.data.AssetItem
import com.aslansari.hypocoin.account.data.AssetRepository
import com.aslansari.hypocoin.account.data.UserResult
import com.aslansari.hypocoin.currency.data.CurrencyRepository

class WalletInfoUseCase (
    private val accountRepository: AccountRepository,
    private val assetRepository: AssetRepository,
    private val currencyRepository: CurrencyRepository,
){

    suspend fun getUserWallet(): UserWallet? {
        val userResult = accountRepository.getAccountWithInfo()
        if (userResult is UserResult.User) {
            val userAssets = assetRepository.getAssets(userResult.uid)
            return UserWallet(userResult, userAssets)
        }
        return null
    }
}

data class UserWallet(
    val user: UserResult.User,
    val userAssets: List<AssetItem>
)