package com.aslansari.hypocoin.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aslansari.hypocoin.account.data.AccountRepository
import com.aslansari.hypocoin.account.data.AssetRepository
import com.aslansari.hypocoin.account.domain.WalletInfoUseCase
import com.aslansari.hypocoin.account.login.domain.LoginUseCase
import com.aslansari.hypocoin.account.login.ui.LoginViewModel
import com.aslansari.hypocoin.account.register.domain.RegisterUseCase
import com.aslansari.hypocoin.account.register.ui.RegisterViewModel
import com.aslansari.hypocoin.account.ui.UserProfileViewModel
import com.aslansari.hypocoin.app.util.AnalyticsReporter
import com.aslansari.hypocoin.currency.domain.CurrencyPriceUseCase
import com.aslansari.hypocoin.repository.CoinRepository
import com.aslansari.hypocoin.viewmodel.CoinViewModel

class ViewModelFactory(
    private val coinRepository: CoinRepository,
    private val accountRepository: AccountRepository,
    private val analyticsReporter: AnalyticsReporter,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            CoinViewModel::class.java -> CoinViewModel(coinRepository) as T
            UserProfileViewModel::class.java -> UserProfileViewModel(
                WalletInfoUseCase(
                    accountRepository,
                    AssetRepository()
                ),
                CurrencyPriceUseCase(coinRepository),
                accountRepository,
                analyticsReporter
            ) as T
            RegisterViewModel::class.java -> RegisterViewModel(
                RegisterUseCase(accountRepository),
                analyticsReporter
            ) as T
            LoginViewModel::class.java -> LoginViewModel(
                LoginUseCase(accountRepository),
                analyticsReporter
            ) as T
            else -> throw IllegalArgumentException("Wrong class type $modelClass")
        }
    }

}