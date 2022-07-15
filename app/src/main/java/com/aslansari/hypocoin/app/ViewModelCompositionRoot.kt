package com.aslansari.hypocoin.app

class ViewModelCompositionRoot(private val activityCompositionRoot: ActivityCompositionRoot) {

    private val coinRepository get() = activityCompositionRoot.coinRepository
    private val currencyRepository get() = activityCompositionRoot.currencyRepository
    private val accountRepository get() = activityCompositionRoot.accountRepository
    private val assetRepository get() = activityCompositionRoot.assetRepository
    val analyticsReporter get() = activityCompositionRoot.analyticsReporter

    val viewModelFactory: ViewModelFactory = ViewModelFactory(
        coinRepository,
        currencyRepository,
        accountRepository,
        assetRepository,
        analyticsReporter,
    )
}