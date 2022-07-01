package com.aslansari.hypocoin.profile;

import com.aslansari.hypocoin.repository.AccountRepository;
import com.aslansari.hypocoin.repository.model.AccountDAO;
import com.aslansari.hypocoin.viewmodel.account.UserProfileAction;
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.rxjava3.observers.DisposableObserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class UserProfileTest {

    @Mock
    AccountDAO accountDAO;

    @Test
    public void userLoginTest() {
        CompletableFuture<UserProfileAction> future = new CompletableFuture<>();
        AccountRepository accountRepository = new AccountRepository(accountDAO);
        UserProfileViewModel userProfileViewModel = new UserProfileViewModel(accountRepository);

        userProfileViewModel.getActionPublishSubject()
                .subscribeWith(new DisposableObserver<UserProfileAction>() {
                    @Override
                    public void onNext(@NotNull UserProfileAction action) {
                        future.complete(action);
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
            assertEquals(UserProfileAction.LOGIN, future.get(5_000, TimeUnit.SECONDS));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
