package com.aslansari.hypocoin.app

class ViewModelCompositionRoot(private val activityCompositionRoot: ActivityCompositionRoot) {

    private val coinRepository get() = activityCompositionRoot.coinRepository
    private val accountRepository get() = activityCompositionRoot.accountRepository
    val analyticsReporter get() = activityCompositionRoot.analyticsReporter

    val viewModelFactory: ViewModelFactory = ViewModelFactory(
        coinRepository,
        accountRepository,
        analyticsReporter,
    )
}