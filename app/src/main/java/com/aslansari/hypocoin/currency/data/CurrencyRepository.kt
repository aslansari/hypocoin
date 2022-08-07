package com.aslansari.hypocoin.currency.data

import android.os.SystemClock
import com.aslansari.hypocoin.currency.data.model.Currency
import com.aslansari.hypocoin.currency.data.model.CurrencyMarketData
import com.aslansari.hypocoin.repository.restapi.CoinAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val ONE_MINUTE = 60 * 1000

class CurrencyRepository(
    private val coinAPI: CoinAPI
) {

    private var currencies: List<Currency> = listOf()
    private var lastFetchDate = 0L

    suspend fun getCurrencies(): List<Currency> = withContext(Dispatchers.IO) {
        if (isExpired()) {
            val list = coinAPI.getCurrencies(30, "id,slug,symbol,name,metrics")
            lastFetchDate = SystemClock.elapsedRealtime()
            currencies = list.assets
            list.assets
        } else {
            currencies
        }
    }

    suspend fun getCurrencyMarketData(id: String): Result<CurrencyMarketData> =
        withContext(Dispatchers.IO) {
            try {
                val currencyMarketData = coinAPI.getCurrencyMarketData(id).data
                Result.success(currencyMarketData)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getRoi(id: String): RoiData {
        return RoiData(coinAPI.getRoiByCurrencyId(id).data.roiData.percentChangeLast1Week)
    }

    private fun isExpired(): Boolean {
        return SystemClock.elapsedRealtime() > lastFetchDate + ONE_MINUTE
    }
}