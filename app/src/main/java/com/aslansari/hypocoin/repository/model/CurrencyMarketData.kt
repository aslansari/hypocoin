package com.aslansari.hypocoin.repository.model

import com.google.gson.annotations.SerializedName

data class CurrencyMarketData(
    @SerializedName("Asset")
    val currency: Currency,
    @SerializedName("market_data")
    val marketData: MarketData,
) {
}