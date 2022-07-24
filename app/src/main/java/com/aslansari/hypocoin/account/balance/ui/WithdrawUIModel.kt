package com.aslansari.hypocoin.account.balance.ui

sealed class WithdrawUIModel{
    data class Error(
        val errorType: ErrorType
    ): WithdrawUIModel()
    object Loading: WithdrawUIModel()
    data class Result(
        val isSuccess: Boolean,
        val withdrawAmount: Long,
        val newBalance: Long,
    ): WithdrawUIModel()

    enum class ErrorType {
        GENERAL,
        INSUFFICIENT_FUNDS,
    }
}