package com.aslansari.hypocoin.repository

import com.aslansari.hypocoin.repository.model.Account
import com.aslansari.hypocoin.repository.model.AccountDAO
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val accountDAO: AccountDAO,
) {

    fun isAccountExists(id: String): Boolean {
        return accountDAO.getAccount(id)
            .onErrorReturnItem(Account(""))
            .blockingGet().id.isNotEmpty()
    }

    fun getAccount(id: String): Single<Account> {
        return accountDAO.getAccount(id)
    }

    suspend fun createAccount(account: Account) = withContext(ioDispatcher) {
        accountDAO.addAccount(account)
    }

    fun updateAccountBalance(id: String?, balance: Long): Completable {
        return accountDAO.updateBalance(id, balance)
    }

    suspend fun isAccountExistsByEmail(email: String): Boolean {
        // todo check email address is recorded in DB
        return true
    }
}