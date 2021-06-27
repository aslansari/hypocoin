package com.aslansari.hypocoin.app;

import android.content.Context;

import com.aslansari.hypocoin.repository.AccountRepository;
import com.aslansari.hypocoin.repository.CoinDatabase;
import com.aslansari.hypocoin.repository.CoinRepository;
import com.aslansari.hypocoin.repository.restapi.CoinAPI;
import com.aslansari.hypocoin.repository.restapi.CoinServiceGenerator;
import com.aslansari.hypocoin.viewmodel.CoinViewModel;
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel;

public final class AppContainer {

    public CoinViewModel coinViewModel;
    public UserProfileViewModel userProfileViewModel;
    private CoinDatabase coinDatabase;
    private CoinAPI coinAPI;
    private CoinRepository coinRepository;
    private AccountRepository accountRepository;

    public AppContainer(Context context) {
        CoinDatabase.init(context);
        coinDatabase = CoinDatabase.getInstance();
        coinAPI = CoinServiceGenerator.createService(CoinAPI.class, CoinAPI.BASE_URL);
        coinRepository = new CoinRepository(coinDatabase, coinAPI);
        accountRepository = new AccountRepository(coinDatabase.accountDAO());

        coinViewModel = new CoinViewModel(coinRepository);
        userProfileViewModel = new UserProfileViewModel(accountRepository);
    }
}
