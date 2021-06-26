package com.aslansari.hypocoin.account;

import com.aslansari.hypocoin.repository.AccountRepository;
import com.aslansari.hypocoin.repository.model.Account;
import com.aslansari.hypocoin.repository.model.AccountDAO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Single;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class AccountTest {

    @Mock
    AccountDAO accountDAO;

    @Test
    public void account_repo_should_get_from_db() {
        given(accountDAO.getAccount(anyString()))
                .willReturn(Single.just(new Account("")));
        AccountRepository accountRepository = new AccountRepository(accountDAO);

        accountRepository.getAccount(anyString());

        then(accountDAO)
                .should()
                .getAccount(anyString());
    }
}
