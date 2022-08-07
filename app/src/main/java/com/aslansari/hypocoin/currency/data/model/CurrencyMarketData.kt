package com.aslansari.hypocoin.currency.data.model

import com.google.gson.annotations.SerializedName

data class CurrencyMarketData(
    @SerializedName("Asset")
    val currency: Currency,
    @SerializedName("market_data")
    val marketData: MarketData,
) {
}