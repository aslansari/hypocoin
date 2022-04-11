package com.aslansari.hypocoin.repository;

import androidx.annotation.NonNull;

import com.aslansari.hypocoin.repository.model.Account;
import com.aslansari.hypocoin.repository.model.AccountDAO;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;


public class AccountRepository {

    private final AccountDAO accountDAO;

    public AccountRepository(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public boolean isAccountExists(@NonNull String id) {
        return !accountDAO.getAccount(id)
                .onErrorReturnItem(new Account(""))
                .blockingGet().getId().isEmpty();
    }

    public Single<Account> getAccount(@NonNull String id) {
        return accountDAO.getAccount(id);
    }

    public Completable createAccount(@NonNull Account account) {
        return accountDAO.addAccount(account);
    }

    public Completable updateAccountBalance(String id, long balance) {
        return accountDAO.updateBalance(id, balance);
    }
}
