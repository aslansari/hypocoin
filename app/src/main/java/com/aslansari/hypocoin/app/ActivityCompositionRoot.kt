package com.aslansari.hypocoin.app

import com.aslansari.hypocoin.account.data.AccountRepository
import com.aslansari.hypocoin.account.data.AssetRepository
import com.aslansari.hypocoin.app.util.AnalyticsReporter
import com.aslansari.hypocoin.currency.data.CurrencyRepository
import com.aslansari.hypocoin.repository.CoinRepository
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ActivityCompositionRoot(private val appContainer: AppContainer) {

    private val databaseReference: DatabaseReference

    init {
        val database = Firebase.database("https://hypo-coin-default-rtdb.europe-west1.firebasedatabase.app")
        databaseReference = database.reference
    }

    val coinRepository: CoinRepository =
        CoinRepository(appContainer.coinDatabase, appContainer.coinAPI)
    val accountRepository: AccountRepository = AccountRepository(
        accountDAO = appContainer.coinDatabase.accountDAO(),
        auth = Firebase.auth,
        database = databaseReference
    )
    val currencyRepository: CurrencyRepository = CurrencyRepository(appContainer.coinAPI)
    val assetRepository: AssetRepository = AssetRepository(currencyRepository, databaseReference)
    val analyticsReporter: AnalyticsReporter by lazy { AnalyticsReporter(Firebase.analytics) }
}