package com.aslansari.hypocoin.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aslansari.hypocoin.R;
import com.aslansari.hypocoin.repository.model.Currency;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CurrencyRecyclerAdapter extends BaseAdapter<Currency> {

    private static final DecimalFormat AMOUNT_FORMAT;

    static {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        AMOUNT_FORMAT = new DecimalFormat("###,##0.00", symbols);
    }

    @Override
    public int getItemViewType(int position) {
        return (isLastPosition(position) && isFooterAdded) ? FOOTER : ITEM;
    }

    @Override
    protected RecyclerView.ViewHolder createHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    protected RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_item, parent, false);
        final CurrencyViewHolder holder = new CurrencyViewHolder(v);

        holder.itemView.setOnClickListener(view -> {
            int adapterPos = holder.getAdapterPosition();
            if(adapterPos != RecyclerView.NO_POSITION){
                // TODO handle click
            }
        });
        return holder;
    }

    @Override
    protected RecyclerView.ViewHolder createFooterViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    protected void bindHeaderViewHolder(RecyclerView.ViewHolder viewHolder) {
    }

    @Override
    protected void bindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final CurrencyRecyclerAdapter.CurrencyViewHolder holder = (CurrencyRecyclerAdapter.CurrencyViewHolder) viewHolder;

        final Currency currency = getItem(position);
        if (currency != null) {
            holder.bind(currency);
        }
    }

    @Override
    protected void bindFooterViewHolder(RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    protected void displayLoadMoreFooter() {

    }

    @Override
    protected void displayErrorFooter() {

    }

    @Override
    public void addFooter() {
        isFooterAdded = true;
        add(new Currency());
    }

    public static class CurrencyViewHolder extends RecyclerView.ViewHolder {

        TextView tvCurrencyName;
        TextView tvCurrencyRatio;

        public CurrencyViewHolder(View view){
            super(view);
            tvCurrencyName = view.findViewById(R.id.tvCurrencyName);
            tvCurrencyRatio = view.findViewById(R.id.tvCurrencyRatio);
        }

        private void bind(Currency currency) {
            tvCurrencyName.setText(currency.symbol);
            tvCurrencyRatio.setText(AMOUNT_FORMAT.format(currency.metrics.marketData.priceUSD));
        }
    }
}
