package com.aslansari.hypocoin.repository.model

import androidx.room.Embedded
import com.google.gson.annotations.SerializedName

data class Metrics(

    @JvmField
    @Embedded
    @SerializedName("market_data")
    var marketData: MarketData? = null
)
