package com.aslansari.hypocoin.register;

import com.aslansari.hypocoin.register.dto.RegisterInput;
import com.aslansari.hypocoin.register.exception.PasswordMismatchException;
import com.aslansari.hypocoin.register.exception.RegisterException;
import com.aslansari.hypocoin.repository.AccountRepository;
import com.aslansari.hypocoin.repository.model.Account;
import com.aslansari.hypocoin.viewmodel.Resource;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;

/**
 * TODO: 6/27/2021 add user repository as dependency to verify user not exists
 */
public class RegisterViewModel {

    private final Register register;
    private final AccountRepository accountRepository;

    public RegisterViewModel(Register register, AccountRepository accountRepository) {
        this.register = register;
        this.accountRepository = accountRepository;
    }

    public Observable<Resource<RegisterInput>> validate(RegisterInput registerInput) {
        return Observable.just(registerInput)
                .flatMap(registerInput1 -> {
                    try {
                        // TODO: 6/27/2021 check if user exists
                        register.validateUsername(registerInput1.getUsername());
                        register.validatePassword(registerInput1.getPassword(), registerInput1.getPasswordRepeat());
                    } catch (IllegalArgumentException | PasswordMismatchException exception) {
                        return Observable.just(Resource.error(registerInput, new RegisterException(exception.getMessage(), exception)));
                    }
                    return Observable.just(Resource.complete(registerInput));
                })
                .delay(3, TimeUnit.SECONDS)
                ;
    }

    public Observable<Resource<Register>> register(RegisterInput registerInput) {
        return Observable.just(registerInput)
                .map(registerInput1 -> {
                    register.validateUsername(registerInput1.getUsername());
                    register.validatePassword(registerInput1.getPassword(), registerInput1.getPasswordRepeat());
                    return registerInput1;
                })
                .map(registerInput1 -> new Account(registerInput1.getUsername()))
                .flatMap(account -> accountRepository.createAccount(account)
                        .andThen(Observable.just(Resource.complete(register))))
                ;
    }

}
