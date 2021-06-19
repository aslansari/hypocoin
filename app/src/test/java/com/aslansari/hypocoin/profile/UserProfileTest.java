package com.aslansari.hypocoin.profile;

import com.aslansari.hypocoin.account.AccountTest;
import com.aslansari.hypocoin.repository.AccountRepository;
import com.aslansari.hypocoin.repository.model.AccountDAO;
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.observers.DisposableObserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UserProfileTest {

    @Test
    public void userLoginTest() {
        CompletableFuture<String> future = new CompletableFuture<>();
        AccountDAO accountDAO = new AccountTest.FakeAccountDAO();
        AccountRepository accountRepository = new AccountRepository(accountDAO);
        UserProfileViewModel userProfileViewModel = new UserProfileViewModel(accountRepository);

        userProfileViewModel.getPublishSubject()
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(@NotNull String s) {
                        future.complete("logged in");
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        fail("on error");
                    }

                    @Override
                    public void onComplete() {
                        fail("on complete");
                    }
                });

        userProfileViewModel.login();

        try {
            assertEquals("logged in",future.get(5_000, TimeUnit.SECONDS));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
