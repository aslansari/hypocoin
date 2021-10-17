package com.aslansari.hypocoin.viewmodel;

import androidx.lifecycle.ViewModel;

import com.aslansari.hypocoin.repository.CoinRepository;
import com.aslansari.hypocoin.repository.model.Currency;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;

/**
 * TODO get saved state from app component
 * TODO refresh currency list
 */
public class CoinViewModel extends ViewModel {

    private final CoinRepository coinRepository;
    private String id;

    public CoinViewModel(CoinRepository coinRepository) {
        this.coinRepository = coinRepository;
    }

    public Flowable<Currency> getCurrencyList() {
        return this.coinRepository.getCurrency();
    }

    public Observable<List<Currency>> getAsyncCurrencyList() {
        return this.coinRepository.getCurrencyList();
    }
}
