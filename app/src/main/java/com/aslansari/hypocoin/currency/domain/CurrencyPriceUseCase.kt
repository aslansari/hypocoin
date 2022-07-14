package com.aslansari.hypocoin.currency.domain

import com.aslansari.hypocoin.repository.CoinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurrencyPriceUseCase(
    private val currencyRepository: CoinRepository,
) {

    // Todo connect with currency repository
    suspend fun getCurrencyPrice(id: String) = withContext(Dispatchers.Default) {
        0L
    }
}