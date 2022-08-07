package com.aslansari.hypocoin.currency.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aslansari.hypocoin.databinding.CurrencyItemBinding

class CurrencyRecyclerAdapter() :
    ListAdapter<CurrencyListItem, CurrencyRecyclerAdapter.CurrencyViewHolder>(diffCallback) {

    var clickListener: ((CurrencyListItem) -> Unit)? = null

    class CurrencyViewHolder(val binding: CurrencyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currencyListItem: CurrencyListItem) {
            binding.item = currencyListItem
        }
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<CurrencyListItem>() {
            override fun areItemsTheSame(
                oldItem: CurrencyListItem,
                newItem: CurrencyListItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CurrencyListItem,
                newItem: CurrencyListItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val binding =
            CurrencyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = CurrencyViewHolder(binding)
        holder.itemView.setOnClickListener { _: View? ->
            val adapterPos = holder.absoluteAdapterPosition
            if (adapterPos != RecyclerView.NO_POSITION) {
                clickListener?.let {
                    it(getItem(adapterPos))
                }
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val currency = getItem(position)
        if (currency != null) {
            holder.bind(currency)
        }
    }
}