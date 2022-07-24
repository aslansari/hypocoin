package com.aslansari.hypocoin.account.balance.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aslansari.hypocoin.account.balance.domain.DepositBalanceUseCase
import com.aslansari.hypocoin.account.balance.domain.WithdrawBalanceUseCase
import com.aslansari.hypocoin.account.data.AccountRepository
import com.aslansari.hypocoin.account.data.UserResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BalanceActionViewModel(
    accountRepository: AccountRepository,
    private val depositBalanceUseCase: DepositBalanceUseCase,
    private val withdrawBalanceUseCase: WithdrawBalanceUseCase,
): ViewModel() {

    val accountState = accountRepository.accountFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            UserResult.Loading
        )

    private val _depositUIModel = MutableLiveData<DepositUIModel>()
    val depositUIModelLiveData = _depositUIModel as LiveData<DepositUIModel>

    private val _withdrawUIModel = MutableLiveData<WithdrawUIModel>()
    val withdrawUIModelLiveData = _withdrawUIModel as LiveData<WithdrawUIModel>

    fun depositBalance(depositAmount: Long) {
        viewModelScope.launch {
            _depositUIModel.value = DepositUIModel.Loading
            val isSuccess = depositBalanceUseCase.deposit(depositAmount)
            val newBalance = if (accountState.value is UserResult.User) {
                (accountState.value as UserResult.User).balance + depositAmount
            } else {
                depositAmount
            }
            _depositUIModel.value = DepositUIModel.Result(
                isSuccess,
                depositAmount,
                newBalance
            )
        }
    }

    fun withdrawBalance(withdrawAmount: Long) {
        viewModelScope.launch {
            _withdrawUIModel.value = WithdrawUIModel.Loading
            if (accountState.value is UserResult.User) {
                if (withdrawAmount > (accountState.value as UserResult.User).balance) {
                    _withdrawUIModel.value = WithdrawUIModel.Error(WithdrawUIModel.ErrorType.INSUFFICIENT_FUNDS)
                } else {
                    val isSuccess: Boolean = withdrawBalanceUseCase.withdraw(withdrawAmount)
                    val newBalance = if (accountState.value is UserResult.User) {
                        (accountState.value as UserResult.User).balance - withdrawAmount
                    } else {
                        withdrawAmount
                    }
                    _withdrawUIModel.value = WithdrawUIModel.Result(
                        isSuccess,
                        withdrawAmount,
                        newBalance
                    )
                }
            } else {
                _withdrawUIModel.value = WithdrawUIModel.Error(WithdrawUIModel.ErrorType.GENERAL)
            }
        }
    }
}
