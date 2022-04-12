package com.aslansari.hypocoin.repository

import com.aslansari.hypocoin.repository.model.Account
import com.aslansari.hypocoin.repository.model.AccountDAO
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class AccountRepository(private val accountDAO: AccountDAO) {
    fun isAccountExists(id: String): Boolean {
        return accountDAO.getAccount(id)
            .onErrorReturnItem(Account(""))
            .blockingGet().id.isNotEmpty()
    }

    fun getAccount(id: String): Single<Account> {
        return accountDAO.getAccount(id)
    }

    fun createAccount(account: Account): Completable {
        return accountDAO.addAccount(account)
    }

    fun updateAccountBalance(id: String?, balance: Long): Completable {
        return accountDAO.updateBalance(id, balance)
    }
}