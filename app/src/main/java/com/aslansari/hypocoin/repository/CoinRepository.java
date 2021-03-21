package com.aslansari.hypocoin.repository;

import com.aslansari.hypocoin.repository.model.Currency;
import com.aslansari.hypocoin.repository.restapi.CoinAPI;
import com.aslansari.hypocoin.repository.restapi.CoinServiceGenerator;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * - TODO implement caching
 * - TODO implement DB operations
 * - TODO dependency injection for db and webservice
 * - TODO single source of truth, fetch data from api, record to db and fetch from db
 *    DB will be the single source of truth
 */
public class CoinRepository {

    private CoinDatabase coinDatabase;
    private CoinAPI coinAPI;
    private Currency currency;

    public CoinRepository() {
        coinDatabase = CoinDatabase.getInstance();
        coinAPI = CoinServiceGenerator.createService(CoinAPI.class, CoinAPI.BASE_URL);
    }

    public Flowable<Currency> getCurrency() {
        if (currency != null) {
            return Flowable.just(currency);
        }

        return coinAPI.getCurrencyAssets(10, "id,slug,symbol,name,metrics")
                .subscribeOn(Schedulers.io())
                .map(currencyAsset -> currencyAsset.assets)
                .flatMap(Observable::fromIterable)
                .toFlowable(BackpressureStrategy.BUFFER)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
