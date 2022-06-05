package com.aslansari.hypocoin.app

import com.aslansari.hypocoin.repository.AccountRepository
import com.aslansari.hypocoin.repository.CoinRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ActivityCompositionRoot(private val appContainer: AppContainer) {

    val coinRepository: CoinRepository = CoinRepository(appContainer.coinDatabase, appContainer.coinAPI)
    val accountRepository: AccountRepository = AccountRepository(accountDAO = appContainer.coinDatabase.accountDAO())
    var auth: FirebaseAuth? = Firebase.auth
}