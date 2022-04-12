package com.aslansari.hypocoin.viewmodel

import com.aslansari.hypocoin.repository.CoinRepository
import androidx.lifecycle.ViewModel
import com.aslansari.hypocoin.repository.model.Currency
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

/**
 * TODO get saved state from app component
 * TODO refresh currency list
 */
class CoinViewModel(private val coinRepository: CoinRepository) : ViewModel() {
    private val id: String? = null
    val currencyList: Flowable<Currency>
        get() = coinRepository.getCurrency()
    val asyncCurrencyList: Observable<List<Currency>>
        get() = coinRepository.currencyList
}