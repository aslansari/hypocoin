package com.aslansari.hypocoin.account.balance.domain

import com.aslansari.hypocoin.account.data.AccountRepository
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DepositBalanceUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {

    suspend fun deposit(currencyAmount: Long): Boolean = suspendCoroutine { cont ->
        accountRepository.depositBalance(currencyAmount) {
            cont.resume(it)
        }
    }
}