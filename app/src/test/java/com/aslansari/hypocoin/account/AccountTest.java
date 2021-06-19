package com.aslansari.hypocoin.account;

import com.aslansari.hypocoin.repository.AccountRepository;
import com.aslansari.hypocoin.repository.model.Account;
import com.aslansari.hypocoin.repository.model.AccountDAO;

import org.junit.Assert;
import org.junit.Test;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;

public class AccountTest {

    @Test
    public void getAccountTest() {
        // Given
        AccountDAO accountDAO = new FakeAccountDAO();
        AccountRepository accountRepository = new AccountRepository(accountDAO);

        // When
        Account account = accountRepository.getAccount("test_id")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .blockingGet();

        // Then
        assertEquals("test_id", account.id);
        assertEquals(100, account.getBalance());
    }

    public static class FakeAccountDAO implements AccountDAO {

        @Override
        public Completable addAccount(Account account) {
            return Completable.complete();
        }

        @Override
        public Single<Account> getAccount(String id) {
            Account account = new Account(id);
            account.setBalance(100);
            return Single.just(account);
        }

        @Override
        public Completable updateBalance(String id, long balance) {
            return null;
        }
    }
}
