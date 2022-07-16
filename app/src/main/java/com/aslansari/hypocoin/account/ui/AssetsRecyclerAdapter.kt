package com.aslansari.hypocoin.account.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aslansari.hypocoin.databinding.LayoutAssetItemBinding


class AssetsRecyclerAdapter() : ListAdapter<AssetListItem, AssetsRecyclerAdapter.AssetItemViewHolder>(AssetDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetItemViewHolder {
        val binding = LayoutAssetItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = AssetItemViewHolder(binding)
        holder.itemView.setOnClickListener {
            val adapterPos = holder.absoluteAdapterPosition
            if (adapterPos != RecyclerView.NO_POSITION) {
                // TODO handle click
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: AssetItemViewHolder, position: Int) {
        val assetItem = getItem(position)
        assetItem?.let {
            holder.bind(it)
        }
    }

    class AssetItemViewHolder(private val binding: LayoutAssetItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(assetItem: AssetListItem) {
            binding.textFieldCurrencySymbol.text = assetItem.symbol
            binding.textFieldCurrencyName.text = assetItem.name
            binding.textFieldCurrencyAmount.text = assetItem.amount
            binding.textFieldCurrencyUnitValue.text = assetItem.priceUSD
        }
    }

    object AssetDiffUtil: DiffUtil.ItemCallback<AssetListItem>() {
        override fun areItemsTheSame(oldItem: AssetListItem, newItem: AssetListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AssetListItem, newItem: AssetListItem): Boolean {
            return oldItem == newItem
        }
    }
}