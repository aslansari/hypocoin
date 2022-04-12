package com.aslansari.hypocoin.repository

import com.aslansari.hypocoin.repository.model.Currency
import com.aslansari.hypocoin.repository.restapi.Asset
import com.aslansari.hypocoin.repository.restapi.CoinAPI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * - TODO implement caching
 * - TODO implement DB operations
 * - TODO single source of truth, fetch data from api, record to db and fetch from db
 * DB will be the single source of truth
 */
class CoinRepository(private val coinDatabase: CoinDatabase, private val coinAPI: CoinAPI) {
    private var currency: List<Currency> = listOf()
    val currencyList: @NonNull Observable<List<Currency>>
        get() = coinAPI.getCurrencyAssets(30, "id,slug,symbol,name,metrics")
            .subscribeOn(Schedulers.io())
            .map { currencyAsset: Asset<Currency> -> storeAssets(currencyAsset) }
            .observeOn(AndroidSchedulers.mainThread())

    fun getCurrency(): Flowable<Currency> {
        return coinAPI.getCurrencyAssets(30, "id,slug,symbol,name,metrics")
            .subscribeOn(Schedulers.io())
            .map { currencyAsset: Asset<Currency> -> storeAssets(currencyAsset) }
            .flatMap { source: List<Currency> -> Observable.fromIterable(source) }
            .toFlowable(BackpressureStrategy.BUFFER)
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun storeAssets(currencyAsset: Asset<Currency>): List<Currency> {
        currency = currencyAsset.assets
        return currencyAsset.assets
    }
}