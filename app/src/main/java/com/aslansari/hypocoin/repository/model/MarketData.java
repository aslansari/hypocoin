package com.aslansari.hypocoin.repository.model;

import com.google.gson.annotations.SerializedName;

public class MarketData {

    @SerializedName("price_usd")
    public double priceUSD;
    @SerializedName("price_btc")
    public double priceBTC;
    @SerializedName("price_eth")
    public double priceETH;
}
