package com.aslansari.hypocoin.account.balance.ui

sealed class DepositUIModel{
    data class Error(
        val errorType: ErrorType
    ): DepositUIModel()
    object Loading: DepositUIModel()
    data class Result(
        val isSuccess: Boolean,
        val depositAmount: Long,
        val newAmount: Long,
    ): DepositUIModel()

    enum class ErrorType {
    }
}