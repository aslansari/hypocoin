package com.aslansari.hypocoin.viewmodel.account;

import androidx.lifecycle.ViewModel;

import com.aslansari.hypocoin.repository.AccountRepository;
import com.aslansari.hypocoin.repository.model.Account;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class UserProfileViewModel extends ViewModel {

    public static final DecimalFormat AMOUNT_FORMAT;

    static {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        AMOUNT_FORMAT = new DecimalFormat("###,##0.00", symbols);
    }

    private final PublishSubject<UserProfileAction> actionPublishSubject;
    private final AccountRepository accountRepository;
    private final boolean isLoggedIn = false;
    private String id;

    public UserProfileViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        actionPublishSubject = PublishSubject.create();
    }

    public void login() {
        actionPublishSubject.onNext(UserProfileAction.LOGIN);
    }

    public void registerRequest() {
        actionPublishSubject.onNext(UserProfileAction.REGISTER_REQUEST);
    }

    public void register() {
        actionPublishSubject.onNext(UserProfileAction.REGISTER);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public Single<Account> getAccount() {
        return accountRepository.getAccount(id);
    }

    public PublishSubject<UserProfileAction> getActionPublishSubject() {
        return actionPublishSubject;
    }
}
