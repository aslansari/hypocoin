package com.aslansari.hypocoin.repository;

import com.aslansari.hypocoin.repository.model.Currency;
import com.aslansari.hypocoin.repository.restapi.Asset;
import com.aslansari.hypocoin.repository.restapi.CoinAPI;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * - TODO implement caching
 * - TODO implement DB operations
 * - TODO single source of truth, fetch data from api, record to db and fetch from db
 * DB will be the single source of truth
 */
public class CoinRepository {

    private final CoinDatabase coinDatabase;
    private final CoinAPI coinAPI;
    private List<Currency> currency;

    public CoinRepository(CoinDatabase coinDatabase, CoinAPI coinAPI) {
        this.coinDatabase = coinDatabase;
        this.coinAPI = coinAPI;
    }

    public Flowable<Currency> getCurrency() {
        if (currency != null) {
            return Flowable.fromIterable(currency);
        }

        return coinAPI.getCurrencyAssets(30, "id,slug,symbol,name,metrics")
                .subscribeOn(Schedulers.io())
                .map(this::storeAssets)
                .flatMap(Observable::fromIterable)
                .toFlowable(BackpressureStrategy.BUFFER)
                .observeOn(AndroidSchedulers.mainThread());
    }

    private List<Currency> storeAssets(Asset<Currency> currencyAsset) {
        currency = currencyAsset.assets;
        return currencyAsset.assets;
    }
}
