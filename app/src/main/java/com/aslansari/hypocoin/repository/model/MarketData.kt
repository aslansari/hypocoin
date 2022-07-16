package com.aslansari.hypocoin.repository.model

import com.google.gson.annotations.SerializedName

data class MarketData(
    @JvmField
    @SerializedName("price_usd")
    var priceUSD: Double = 0.0,

    @SerializedName("price_btc")
    var priceBTC: Double = 0.0,

    @SerializedName("price_eth")
    var priceETH: Double = 0.0,
)