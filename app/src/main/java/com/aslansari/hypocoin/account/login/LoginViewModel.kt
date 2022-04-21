package com.aslansari.hypocoin.account.login

import androidx.lifecycle.*
import com.aslansari.hypocoin.viewmodel.login.LoginUIModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel: ViewModel() {

    private var registerClickListener: (() -> Unit)? = null
    private var _loginState = MutableLiveData<LoginUIModel>()
    val loginUIState: LiveData<LoginUIModel> = _loginState

    private var _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    val accountId = MutableLiveData<String>()

    val loginButtonEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(accountId) { value = it.isNotBlank()}
        addSource(_loading) { loading -> value = loading.not() }
    }

    fun registerClicked() {
        registerClickListener?.invoke()
    }

    fun onRegisterClicked(registerClickListener: () -> Unit) {
        this.registerClickListener = registerClickListener
    }

    fun loginClicked(id: String) {
        Timber.i("login clicked with id:$id")
        viewModelScope.launch {
            _loading.value = true
            delay(3000)
            _loading.value = false
        }
    }
}
