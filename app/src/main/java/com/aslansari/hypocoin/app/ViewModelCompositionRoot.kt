package com.aslansari.hypocoin.app

import com.google.firebase.auth.FirebaseAuth

class ViewModelCompositionRoot(private val activityCompositionRoot: ActivityCompositionRoot) {

    init {
        FirebaseAuth.getInstance().useEmulator("192.168.1.28", 9099)
    }

    private val coinRepository get() = activityCompositionRoot.coinRepository
    private val accountRepository get() = activityCompositionRoot.accountRepository
    private val auth get() = activityCompositionRoot.auth

    val viewModelFactory: ViewModelFactory = ViewModelFactory(
        coinRepository,
        accountRepository,
        auth,
    )
}