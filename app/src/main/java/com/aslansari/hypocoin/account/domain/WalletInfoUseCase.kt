package com.aslansari.hypocoin.account.domain

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.aslansari.hypocoin.account.data.AccountRepository
import com.aslansari.hypocoin.account.data.AssetItem
import com.aslansari.hypocoin.account.data.AssetRepository
import com.aslansari.hypocoin.account.data.UserResult
import com.aslansari.hypocoin.currency.data.CurrencyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class WalletInfoUseCase (
    private val accountRepository: AccountRepository,
    private val assetRepository: AssetRepository,
    private val currencyRepository: CurrencyRepository,
){

    fun wallet() = accountRepository.accountFlow.combine(assetRepository.assetItems()) { user, asset ->
        if (user is UserResult.User) {
            return@combine UserWallet(user, asset)
        }
        return@combine null
    }.stateIn(
        ProcessLifecycleOwner.get().lifecycleScope,
        SharingStarted.WhileSubscribed(),
        null
    )
}

data class UserWallet(
    val user: UserResult.User,
    val userAssets: List<AssetItem>
)