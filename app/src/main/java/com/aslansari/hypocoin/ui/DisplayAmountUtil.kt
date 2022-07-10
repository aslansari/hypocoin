package com.aslansari.hypocoin.ui

import java.text.DecimalFormat

object DisplayAmountUtil {

    private val decimalFormat = DecimalFormat("###,##0.00")

    /**
     * Get dollar amount for display
     * @param amount amount in cents
     * @return return formatted string {$12.45 USD}
     */
    fun getDollarAmount(amount: Long): String {
        val dollarAmount = decimalFormat.format(amount.toDouble()/100)
        return "$${dollarAmount} USD"
    }
}