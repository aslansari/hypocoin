package com.aslansari.hypocoin.account.balance.ui

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

private const val MAX_DECIMAL = 2
private const val MAX_LENGTH = 20

class CurrencyTextWatcher(
    private val editText: EditText,
    private val prefix: String
) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        // do nothing
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        // do nothing
    }

    override fun afterTextChanged(editable: Editable) {
        val str = editable.toString()
        if (str.length < prefix.length) {
            editText.setText(prefix)
            editText.setSelection(prefix.length)
            return
        }
        if (str == prefix) {
            return
        }
        // cleanString this the string which not contain prefix and ,
        val cleanString = str.replace(prefix, "").replace("[,]".toRegex(), "")
        val formattedString: String = if (cleanString.contains(".")) {
            formatDecimal(cleanString)
        } else {
            formatInteger(cleanString)
        }
        editText.removeTextChangedListener(this) // Remove listener
        editText.setText(formattedString)
        handleSelection()
        editText.addTextChangedListener(this) // Add back the listener
    }

    private fun formatInteger(str: String): String {
        val parsed = BigDecimal(str)
        val formatter = DecimalFormat("$prefix#,###", DecimalFormatSymbols(Locale.US))
        return formatter.format(parsed)
    }

    private fun formatDecimal(str: String): String {
        if (str == ".") {
            return "$prefix."
        }
        val parsed = BigDecimal(str)
        val formatter = DecimalFormat(
            prefix + "#,###." + getDecimalPattern(str),
            DecimalFormatSymbols(Locale.US)
        )
        formatter.roundingMode = RoundingMode.DOWN
        return formatter.format(parsed)
    }

    /**
     * It will return suitable pattern for format decimal
     * For example: 10.2 -> return 0 | 10.23 -> return 00, | 10.235 -> return 000
     */
    private fun getDecimalPattern(str: String): String {
        val decimalCount = str.length - str.indexOf(".") - 1
        Timber.d("Decimal Count = $decimalCount")
        val decimalPattern = StringBuilder()
        val count = decimalCount.coerceAtMost(MAX_DECIMAL)
        count.downTo(1).forEach { _ ->
            decimalPattern.append("0")
        }
        Timber.d("Decimal pattern $decimalPattern")
        return decimalPattern.toString()
    }

    private fun handleSelection() {
        editText.setSelection(editText.text.length.coerceAtMost(MAX_LENGTH))
    }

    fun getCurrencyAmount(): Long {
        val cleanString = editText.text.toString().replace("$", "").replace("[,]".toRegex(), "")
        return if (cleanString.isBlank()) {
            0L
        } else {
            (cleanString.toDouble() * 100).toLong()
        }
    }
}
