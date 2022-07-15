package com.aslansari.hypocoin.account.domain

import com.aslansari.hypocoin.account.data.AccountRepository
import com.aslansari.hypocoin.account.data.AssetRepository
import com.aslansari.hypocoin.account.data.UserResult
import com.aslansari.hypocoin.currency.data.CurrencyRepository
import com.aslansari.hypocoin.currency.data.RoiData
import com.aslansari.hypocoin.currency.domain.CurrencyPriceUseCase

class NetWorthUseCase(
    private val assetRepository: AssetRepository,
    private val currencyRepository: CurrencyRepository,
    private val accountRepository: AccountRepository,
    private val currencyPriceUseCase: CurrencyPriceUseCase,
) {

    suspend fun get(uid: String): Long {
        val balance = when (val user = accountRepository.getAccountWithInfo()) {
            is UserResult.User -> {user.balance}
            else -> {0L}
        }
        val assetsAmount = assetRepository.getAssets(uid).sumOf {
            it.amount.times(currencyPriceUseCase.getCurrencyPrice(it.id))
        }

        return balance + assetsAmount.toLong()
    }

    suspend fun getRoiData(uid: String): RoiData {
        val roiPairs = assetRepository.getAssets(uid).map {
            val currentValue = currencyPriceUseCase.getCurrencyPrice(it.id) * it.amount
            Pair(currentValue, 1 + it.roiData.percentChangeLast1Week)
        }
        val totalCurrentValues = roiPairs.sumOf { it.first }
        val totalOldValues = roiPairs.sumOf { it.first / it.second }
        val roi = (totalCurrentValues / totalOldValues) - 1
        return RoiData(roi)
    }
}
