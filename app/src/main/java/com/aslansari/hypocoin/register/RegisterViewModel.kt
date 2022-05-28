package com.aslansari.hypocoin.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 *
 */
class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _registerUIState = MutableLiveData(RegisterUIState())
    val registerUIState: LiveData<RegisterUIState> = _registerUIState

    fun onTextChange() {
        viewModelScope.launch {
            _registerUIState.value = defaultRegisterUIState()
        }
    }

    fun onSubmitEmail(email: String?) {
        viewModelScope.launch {
            _registerUIState.value = (RegisterUIState(loading = true, buttonEnabled = false))
            if (email.isNullOrBlank()) {
                _registerUIState.value = RegisterUIState(error = RegisterStatus.INPUT_EMPTY)
            } else {
                val isValid = registerUseCase.validateEmail(email)
                if (isValid) {
                    val isExists = registerUseCase.checkEmail(email)
                    _registerUIState.value = if (isExists) {
                        RegisterUIState(error = RegisterStatus.USER_ALREADY_EXISTS,
                            buttonEnabled = false)
                    } else {
                        defaultRegisterUIState()
                    }
                } else {
                    _registerUIState.value =
                        RegisterUIState(error = RegisterStatus.INPUT_FORMAT_WRONG)
                }
            }
        }
    }

    fun registerWithGoogle() {

    }
}

data class RegisterUIState(
    val loading: Boolean = false,
    val error: RegisterStatus = RegisterStatus.NO_ERROR,
    val buttonEnabled: Boolean = true,
)

fun defaultRegisterUIState() = RegisterUIState()

enum class RegisterStatus {
    NO_ERROR,
    USER_ALREADY_EXISTS,
    INPUT_FORMAT_WRONG,
    INPUT_EMPTY
}