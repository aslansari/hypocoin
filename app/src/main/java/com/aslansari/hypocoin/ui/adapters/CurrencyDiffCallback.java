package com.aslansari.hypocoin.ui.adapters;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.aslansari.hypocoin.repository.model.Currency;

import java.util.List;

public class CurrencyDiffCallback extends DiffUtil.Callback {

    private final List<Currency> newCurrencyList;
    private final List<Currency> oldCurrencyList;

    public CurrencyDiffCallback(List<Currency> newCurrencyList, List<Currency> oldCurrencyList) {
        this.newCurrencyList = newCurrencyList;
        this.oldCurrencyList = oldCurrencyList;
    }

    @Override
    public int getOldListSize() {
        return oldCurrencyList.size();
    }

    @Override
    public int getNewListSize() {
        return newCurrencyList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return newCurrencyList.get(newItemPosition).id.equals(oldCurrencyList.get(oldItemPosition).id);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        double newUsdPrice = newCurrencyList.get(newItemPosition).metrics.marketData.priceUSD;
        double oldUsdPrice = oldCurrencyList.get(oldItemPosition).metrics.marketData.priceUSD;
        return newUsdPrice - oldUsdPrice < 0.01;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
