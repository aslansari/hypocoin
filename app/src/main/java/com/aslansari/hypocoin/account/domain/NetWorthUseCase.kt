package com.aslansari.hypocoin.account.domain

import com.aslansari.hypocoin.account.data.AccountRepository
import com.aslansari.hypocoin.account.data.AssetRepository
import com.aslansari.hypocoin.currency.data.RoiData
import com.aslansari.hypocoin.repository.CoinRepository

class NetWorthUseCase(
    private val assetRepository: AssetRepository,
    private val currencyRepository: CoinRepository,
    private val accountRepository: AccountRepository,
) {

    fun get(): Long {
        // balance + assets and their usd price
        return 45123L
    }

    fun getRoiData(): RoiData {
        // (usd price, rate) pairs ->
        return RoiData(0.12345678)
    }
}
