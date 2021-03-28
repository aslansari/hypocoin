package com.aslansari.hypocoin.repository;

import com.aslansari.hypocoin.repository.model.Currency;
import com.aslansari.hypocoin.repository.restapi.Asset;
import com.aslansari.hypocoin.repository.restapi.CoinAPI;
import com.aslansari.hypocoin.repository.restapi.CoinServiceGenerator;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * - TODO implement caching
 * - TODO implement DB operations
 * - TODO single source of truth, fetch data from api, record to db and fetch from db
 *    DB will be the single source of truth
 */
public class CoinRepository {

    private CoinDatabase coinDatabase;
    private CoinAPI coinAPI;
    private List<Currency> currency;

    public CoinRepository(CoinDatabase coinDatabase, CoinAPI coinAPI) {
        this.coinDatabase = coinDatabase;
        this.coinAPI = coinAPI;
    }

    public Flowable<Currency> getCurrency() {
        if (currency != null) {
            return Flowable.fromIterable(currency);
        }

        return coinAPI.getCurrencyAssets(10, "id,slug,symbol,name,metrics")
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
