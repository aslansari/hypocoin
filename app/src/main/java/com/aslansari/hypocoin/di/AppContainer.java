package com.aslansari.hypocoin.di;

import android.content.Context;

import com.aslansari.hypocoin.repository.CoinDatabase;
import com.aslansari.hypocoin.repository.CoinRepository;
import com.aslansari.hypocoin.repository.restapi.CoinAPI;
import com.aslansari.hypocoin.repository.restapi.CoinServiceGenerator;
import com.aslansari.hypocoin.viewmodel.CoinViewModel;

public final class AppContainer {

    private CoinDatabase coinDatabase;
    private CoinAPI coinAPI;
    private CoinRepository coinRepository;

    public CoinViewModel coinViewModel;

    public AppContainer(Context context) {
        CoinDatabase.init(context);
        coinDatabase = CoinDatabase.getInstance();
        coinAPI = CoinServiceGenerator.createService(CoinAPI.class, CoinAPI.BASE_URL);
        coinRepository = new CoinRepository(coinDatabase, coinAPI);

        coinViewModel = new CoinViewModel(coinRepository);
    }
}
