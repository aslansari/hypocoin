package com.aslansari.hypocoin.account.balance.domain

import kotlinx.coroutines.delay

class DepositBalanceUseCase {

    suspend fun deposit(currencyAmount: Long): Boolean {
        delay(1000)
        return true
    }
}