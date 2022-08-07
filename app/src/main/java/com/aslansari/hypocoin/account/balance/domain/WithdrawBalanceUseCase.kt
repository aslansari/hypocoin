package com.aslansari.hypocoin.account.balance.domain

import com.aslansari.hypocoin.account.data.AccountRepository
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WithdrawBalanceUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {

    suspend fun withdraw(currencyAmount: Long): Boolean = suspendCoroutine { cont ->
        accountRepository.withdrawBalance(currencyAmount) {
            cont.resume(it)
        }
    }
}