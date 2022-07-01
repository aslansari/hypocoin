package com.aslansari.hypocoin.account.login

import androidx.lifecycle.*
import com.aslansari.hypocoin.viewmodel.login.LoginError
import com.aslansari.hypocoin.viewmodel.login.LoginUIModel
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel(
    private val loginUseCase: LoginUseCase
): ViewModel() {

    private var _loginUIState = MutableLiveData<LoginUIModel>()
    val loginUIState: LiveData<LoginUIModel> = _loginUIState

    private var _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    val accountId = MutableLiveData<String>()

    val loginButtonEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(accountId) { value = it.isNotBlank() }
        addSource(_loading) { loading -> value = loading.not() }
    }

    fun onLoginClick(email: String, password: String) {
        Timber.d("Login clicked")
        _loginUIState.value = LoginUIModel.Loading
        viewModelScope.launch {
            loginUseCase.loginUser(email, password)
            _loginUIState.value = LoginUIModel.Idle
        }
    }

    fun onEmailTextChange() {
        _loginUIState.value = LoginUIModel.Idle
    }

    fun onSubmitEmailClick(email: String) {
        if (loginUseCase.validateEmail(email)) {
            loginUseCase.checkEmail(email) {
                if (it) {
                    _loginUIState.value = LoginUIModel.Result
                } else {
                    _loginUIState.value = LoginUIModel.Error(LoginError.EMAIL_DOES_NOT_EXISTS)
                }
            }
        } else {
            _loginUIState.value = LoginUIModel.Error(LoginError.EMAIL_INVALID)
        }
    }

    fun onForgotPasswordClick(email: String) {

    }

    fun onGoogleLoginClick() {

    }
}
