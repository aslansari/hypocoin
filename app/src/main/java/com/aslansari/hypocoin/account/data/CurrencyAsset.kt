package com.aslansari.hypocoin.account.data

data class CurrencyAsset(
    val id: String,
    val symbol: String,
    val name: String,
    val slug: String,
    val amount: Double,
)