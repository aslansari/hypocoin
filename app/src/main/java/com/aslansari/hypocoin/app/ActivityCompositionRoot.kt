package com.aslansari.hypocoin.app

import com.aslansari.hypocoin.repository.AccountRepository
import com.aslansari.hypocoin.repository.CoinRepository

class ActivityCompositionRoot(private val appContainer: AppContainer) {

    val coinRepository: CoinRepository = CoinRepository(appContainer.coinDatabase, appContainer.coinAPI)
    val accountRepository: AccountRepository = AccountRepository(appContainer.coinDatabase.accountDAO())
}