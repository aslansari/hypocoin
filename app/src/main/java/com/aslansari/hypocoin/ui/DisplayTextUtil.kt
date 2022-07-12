package com.aslansari.hypocoin.ui

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object DisplayTextUtil {

    object Amount {
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

        fun getAmountForNetWorth(amount: Long): String = when (amount) {
            0L -> "$ --"
            else -> getDollarAmount(amount)
        }
    }

    object Date {

        private val dateFormat by lazy { SimpleDateFormat("dd MM yyyy") }
        private val timeFormat by lazy { SimpleDateFormat("MMMM dd 'at' HH:mm") }

        private fun getFormattedDateTime(timestamp: Long?, dateFormat: SimpleDateFormat): String {
            if (timestamp == null) {
                return ""
            }
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            return dateFormat.format(calendar.time)
        }

        fun getFormattedTime(timestamp: Long?): String {
            return getFormattedDateTime(timestamp, timeFormat)
        }

        fun getFormattedDate(timestamp: Long?): String {
            return getFormattedDateTime(timestamp, dateFormat)
        }
    }
}