package com.aslansari.hypocoin.account.domain

import com.aslansari.hypocoin.account.data.AccountRepository
import com.aslansari.hypocoin.account.data.AssetRepository
import com.aslansari.hypocoin.account.data.UserResult
import com.aslansari.hypocoin.currency.data.CurrencyRepository
import com.aslansari.hypocoin.currency.data.RoiData
import com.aslansari.hypocoin.currency.domain.CurrencyPriceUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class NetWorthUseCase(
    private val assetRepository: AssetRepository,
    private val currencyRepository: CurrencyRepository,
    private val accountRepository: AccountRepository,
    private val currencyPriceUseCase: CurrencyPriceUseCase,
) {

    val roiDataFlow = assetRepository.assetItems().map { assetItemList ->
        val roiPairs = assetItemList.map {
            val currentValue = currencyPriceUseCase.getCurrencyPrice(it.id) * it.amount
            Pair(currentValue, 1 + (it.roiData.percentChangeLast1Week / 100))
        }
        val totalCurrentValues = roiPairs.sumOf { it.first }
        val totalOldValues = roiPairs.sumOf { it.first / it.second }
        RoiData((totalCurrentValues / totalOldValues) - 1)
    }

    val netWorthFlow: Flow<Long> = accountRepository.accountFlow.combine(assetRepository.assetItems()) {
        result, assets ->
        when (result) {
            is UserResult.User -> {
                val assetsAmount = assets.sumOf {
                    it.amount.times(currencyPriceUseCase.getCurrencyPrice(it.id))
                }
                result.balance + assetsAmount.toLong()
            }
            else -> {0L}
        }
    }
}
