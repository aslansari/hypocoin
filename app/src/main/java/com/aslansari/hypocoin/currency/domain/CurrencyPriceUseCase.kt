package com.aslansari.hypocoin.currency.domain

import com.aslansari.hypocoin.currency.data.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurrencyPriceUseCase(
    private val currencyRepository: CurrencyRepository,
) {

    suspend fun getCurrencyPrice(id: String): Long = withContext(Dispatchers.Default) {
        val result = currencyRepository.getCurrencyMarketData(id)
        if (result.isSuccess) {
            ((result.getOrNull()?.marketData?.priceUSD ?: 0.0) * 100).toLong()
        } else {
            0L
        }
    }
}