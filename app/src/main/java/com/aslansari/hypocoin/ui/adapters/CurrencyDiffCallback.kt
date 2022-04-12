package com.aslansari.hypocoin.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.aslansari.hypocoin.repository.model.Currency

class CurrencyDiffCallback(
    private val newCurrencyList: List<Currency>,
    private val oldCurrencyList: List<Currency>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldCurrencyList.size
    }

    override fun getNewListSize(): Int {
        return newCurrencyList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newCurrencyList[newItemPosition].id == oldCurrencyList[oldItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newUsdPrice = newCurrencyList[newItemPosition].metrics!!.marketData!!.priceUSD
        val oldUsdPrice = oldCurrencyList[oldItemPosition].metrics!!.marketData!!.priceUSD
        return newUsdPrice - oldUsdPrice < 0.01
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}