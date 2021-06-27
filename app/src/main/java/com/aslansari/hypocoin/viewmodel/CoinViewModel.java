package com.aslansari.hypocoin.viewmodel;

import androidx.lifecycle.ViewModel;

import com.aslansari.hypocoin.repository.CoinRepository;
import com.aslansari.hypocoin.repository.model.Currency;

import io.reactivex.rxjava3.core.Flowable;

/**
 * TODO get saved state from app component
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
}
