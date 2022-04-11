package com.aslansari.hypocoin.repository.model

import com.google.gson.annotations.SerializedName

data class Metrics(

    @JvmField
    @SerializedName("market_data")
    var marketData: MarketData? = null
)
