package com.aslansari.hypocoin.viewmodel.account;

import com.aslansari.hypocoin.repository.model.Account;

public class AccountUIModel {

    public boolean isLoading;
    public boolean isLoggedIn;
    public Account account;

    public static AccountUIModel idle() {
        // TODO create appropriate constructors
        AccountUIModel model = new AccountUIModel();
        model.account = null;
        model.isLoading = false;
        model.isLoggedIn = false;
        return model;
    }


}
