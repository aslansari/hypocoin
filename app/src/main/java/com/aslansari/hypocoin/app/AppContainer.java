package com.aslansari.hypocoin.app;

import android.content.Context;

import com.aslansari.hypocoin.register.Register;
import com.aslansari.hypocoin.register.RegisterViewModel;
import com.aslansari.hypocoin.repository.AccountRepository;
import com.aslansari.hypocoin.repository.CoinDatabase;
import com.aslansari.hypocoin.repository.CoinRepository;
import com.aslansari.hypocoin.repository.restapi.CoinAPI;
import com.aslansari.hypocoin.repository.restapi.CoinServiceGenerator;
import com.aslansari.hypocoin.viewmodel.CoinViewModel;
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel;

public final class AppContainer {

    public final CoinViewModel coinViewModel;
    public final UserProfileViewModel userProfileViewModel;
    public final RegisterViewModel registerViewModel;
    private final CoinDatabase coinDatabase;
    private final CoinAPI coinAPI;
    private final CoinRepository coinRepository;
    private final AccountRepository accountRepository;

    public AppContainer(Context context) {
        coinDatabase = CoinDatabase.build(context);
        coinAPI = CoinServiceGenerator.createService(CoinAPI.class, CoinAPI.BASE_URL);
        coinRepository = new CoinRepository(coinDatabase, coinAPI);
        accountRepository = new AccountRepository(coinDatabase.accountDAO());

        coinViewModel = new CoinViewModel(coinRepository);
        userProfileViewModel = new UserProfileViewModel(accountRepository);
        Register register = new Register();
        registerViewModel = new RegisterViewModel(register, accountRepository);
    }
}
