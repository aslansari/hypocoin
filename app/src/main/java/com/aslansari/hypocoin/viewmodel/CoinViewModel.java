package com.aslansari.hypocoin.viewmodel;

import androidx.lifecycle.ViewModel;

import com.aslansari.hypocoin.repository.CoinRepository;
import com.aslansari.hypocoin.repository.model.Currency;

import io.reactivex.Flowable;

/**
 * TODO get saved state from app component
 */
public class CoinViewModel extends ViewModel {

    private String id;
    private CoinRepository coinRepository;

    public CoinViewModel() {
        this.coinRepository = new CoinRepository();
    }

    public Flowable<Currency> getCurrencyList() {
        return this.coinRepository.getCurrency();
    }
}
