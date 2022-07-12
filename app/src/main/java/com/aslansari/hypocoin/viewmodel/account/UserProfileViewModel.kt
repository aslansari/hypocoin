package com.aslansari.hypocoin.viewmodel.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aslansari.hypocoin.app.util.AnalyticsReporter
import com.aslansari.hypocoin.repository.AccountRepository
import com.aslansari.hypocoin.repository.SendVerificationEmailTask
import com.aslansari.hypocoin.repository.UserResult
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val accountRepository: AccountRepository,
    private val analyticsReporter: AnalyticsReporter,
) : ViewModel() {

    private val _userInfoUIModel = MutableLiveData<UserInfoUIModel>()
    val userInfoUIModelLiveData = _userInfoUIModel as LiveData<UserInfoUIModel>

    fun getUserInfo() {
        viewModelScope.launch {
            _userInfoUIModel.value = UserInfoUIModel.Loading
            accountRepository.getAccountWithInfo {
                when(it) {
                    UserResult.Error -> {
                        _userInfoUIModel.value = UserInfoUIModel.Error
                    }
                    is UserResult.User -> {
                        _userInfoUIModel.value = UserInfoUIModel.User(it)
                    }
                    else -> {
                        _userInfoUIModel.value = UserInfoUIModel.Error
                    }
                }
            }
        }
    }

    fun isLoggedIn(): Boolean {
        return accountRepository.isLoggedIn()
    }

    fun logout(completeListener: () -> Unit) {
        accountRepository.logout(completeListener)
    }

    fun sendVerificationEmail(completeListener: (SendVerificationEmailTask) -> Unit) {
        accountRepository.sendVerificationEmail(completeListener)
    }

    fun updateDisplayNumber(displayName: String, completeListener: (Boolean) -> Unit) {
        accountRepository.updateDisplayName(displayName, completeListener)
    }

    fun updateEmail(email: String, completeListener: (Boolean) -> Unit) {
        accountRepository.updateEmail(email, completeListener)
    }
}

sealed class UserInfoUIModel {
    object Error: UserInfoUIModel()
    data class User(
        val data: UserResult.User,
    ): UserInfoUIModel()
    object Loading: UserInfoUIModel()
}