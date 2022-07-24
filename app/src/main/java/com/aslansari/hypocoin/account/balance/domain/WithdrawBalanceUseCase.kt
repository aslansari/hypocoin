package com.aslansari.hypocoin.account.balance.domain

import kotlinx.coroutines.delay

class WithdrawBalanceUseCase {

    suspend fun withdraw(currencyAmount: Long): Boolean {
        delay(1000)
        return true
    }
}