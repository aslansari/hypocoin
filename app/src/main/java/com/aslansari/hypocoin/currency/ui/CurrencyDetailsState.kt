package com.aslansari.hypocoin.currency.ui

import com.aslansari.hypocoin.account.ui.RoiChip

sealed class CurrencyDetailsState {
    object Loading : CurrencyDetailsState()
    object Error : CurrencyDetailsState()
    data class Result(
        val symbol: String,
        val currencyValue: Long,
        val walletInfo: Double,
        val roiChip: RoiChip,
        val allTimeHigh: AllTimeHigh,
    ) : CurrencyDetailsState()

    data class AllTimeHigh(
        val price: Long,
        val date: String,
        val days: Int,
        val percentDown: Double,
    )
}