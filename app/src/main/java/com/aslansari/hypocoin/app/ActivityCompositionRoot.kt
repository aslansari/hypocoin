package com.aslansari.hypocoin.app

import com.aslansari.hypocoin.app.util.AnalyticsReporter
import com.aslansari.hypocoin.repository.AccountRepository
import com.aslansari.hypocoin.repository.CoinRepository
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ActivityCompositionRoot(private val appContainer: AppContainer) {

    val coinRepository: CoinRepository = CoinRepository(appContainer.coinDatabase, appContainer.coinAPI)
    val accountRepository: AccountRepository = AccountRepository(accountDAO = appContainer.coinDatabase.accountDAO(), auth = Firebase.auth)
    val analyticsReporter: AnalyticsReporter by lazy { AnalyticsReporter(Firebase.analytics) }
}