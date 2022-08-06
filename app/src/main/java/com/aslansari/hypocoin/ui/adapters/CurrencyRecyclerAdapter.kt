package com.aslansari.hypocoin.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.repository.model.Currency
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class CurrencyRecyclerAdapter : BaseAdapter<Currency?>() {

    var clickListener: ((Currency?) -> Unit)? = null

    companion object {
        private val AMOUNT_FORMAT: DecimalFormat =
            DecimalFormat("###,##0.00", DecimalFormatSymbols.getInstance(Locale.US))
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLastPosition(position) && isFooterAdded) {
            FOOTER
        } else {
            ITEM
        }
    }

    override fun createHeaderViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder? {
        return null
    }

    override fun createItemViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder? {
        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.currency_item, parent, false)
        val holder = CurrencyViewHolder(v)
        holder.itemView.setOnClickListener { view: View? ->
            val adapterPos = holder.absoluteAdapterPosition
            if (adapterPos != RecyclerView.NO_POSITION) {
                clickListener?.let {
                    it(getItem(adapterPos))
                }
            }
        }
        return holder
    }

    override fun createFooterViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder? {
        return null
    }

    override fun bindHeaderViewHolder(viewHolder: RecyclerView.ViewHolder?) {}
    override fun bindItemViewHolder(viewHolder: RecyclerView.ViewHolder?, position: Int) {
        val holder = viewHolder as CurrencyViewHolder?
        val currency = getItem(position)
        if (currency != null) {
            holder!!.bind(currency)
        }
    }

    override fun bindFooterViewHolder(viewHolder: RecyclerView.ViewHolder?) {}
    override fun displayLoadMoreFooter() {}
    override fun displayErrorFooter() {}
    override fun addFooter() {
        isFooterAdded = true
        add(Currency())
    }

    class CurrencyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvCurrencyName: TextView
        var tvCurrencyRatio: TextView
        fun bind(currency: Currency) {
            tvCurrencyName.text = currency.symbol
            tvCurrencyRatio.text = AMOUNT_FORMAT.format(
                currency.metrics!!.marketData!!.priceUSD
            )
        }

        init {
            tvCurrencyName = view.findViewById(R.id.tvCurrencyName)
            tvCurrencyRatio = view.findViewById(R.id.tvCurrencyRatio)
        }
    }

    fun updateList(currencyList: List<Currency>) {
        val diffResult = DiffUtil.calculateDiff(
            CurrencyDiffCallback(
                currencyList,
                items.toList() as List<Currency>
            )
        )
        items = currencyList.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }
}