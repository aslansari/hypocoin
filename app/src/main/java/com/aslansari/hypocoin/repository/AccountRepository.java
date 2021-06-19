package com.aslansari.hypocoin.repository;

import androidx.annotation.NonNull;

import com.aslansari.hypocoin.repository.model.Account;
import com.aslansari.hypocoin.repository.model.AccountDAO;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AccountRepository {

    private final AccountDAO accountDAO;

    public AccountRepository(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
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
