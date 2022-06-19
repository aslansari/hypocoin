package com.aslansari.hypocoin.account.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aslansari.hypocoin.viewmodel.login.LoginUIModel

class LoginViewModel: ViewModel() {

    private var _loginState = MutableLiveData<LoginUIModel>()
    val loginUIState: LiveData<LoginUIModel> = _loginState

    private var _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    val accountId = MutableLiveData<String>()

    val loginButtonEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(accountId) { value = it.isNotBlank()}
        addSource(_loading) { loading -> value = loading.not() }
    }

    fun onLoginClick(email: String, password: String) {

    }

    fun onSubmitEmailClick(email: String) {
        // validate and return ui state
    }

    fun onForgotPasswordClick(email: String) {

    }

    fun onGoogleLoginClick() {

    }
}
