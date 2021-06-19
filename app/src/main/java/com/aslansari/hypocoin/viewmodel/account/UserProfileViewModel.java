package com.aslansari.hypocoin.viewmodel.account;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.aslansari.hypocoin.repository.AccountRepository;
import com.aslansari.hypocoin.repository.model.Account;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class UserProfileViewModel extends ViewModel {

    public static final DecimalFormat AMOUNT_FORMAT;
    private boolean isLoggedIn = false;
    private Single<String> userLoginSingle;
    PublishSubject<String> publishSubject;

    static {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        AMOUNT_FORMAT = new DecimalFormat("###,##0.00", symbols);
    }

    private String id;
    private final AccountRepository accountRepository;

    public UserProfileViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        publishSubject = PublishSubject.create();
    }

    public void login() {
        publishSubject.onNext("logged in");
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public Single<Account> getAccount() {
        return accountRepository.getAccount(id);
    }

    public Completable createAccount(@NonNull String id) {
        Account account = new Account(id);
        return accountRepository.createAccount(account)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public PublishSubject<String> getPublishSubject() {
        return publishSubject;
    }
}
