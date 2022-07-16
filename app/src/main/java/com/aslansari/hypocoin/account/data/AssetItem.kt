package com.aslansari.hypocoin.account.data

import com.aslansari.hypocoin.currency.data.RoiData

data class AssetItem(
    val id: String,
    val symbol: String,
    val name: String,
    val amount: Double,
    val roiData: RoiData,
)
