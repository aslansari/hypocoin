package com.aslansari.hypocoin.register

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.register.exception.PasswordMismatchException
import com.aslansari.hypocoin.repository.AccountRepository
import com.aslansari.hypocoin.repository.model.Account
import kotlinx.coroutines.launch

/**
 *
 */
class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val accountRepository: AccountRepository
): ViewModel() {

    private val _registerUIState = MutableLiveData(RegisterUIState())
    val registerUIState: LiveData<RegisterUIState> = _registerUIState

    val accountId = MutableLiveData<String>()
    val passwordFirst = MutableLiveData<String>()
    val passwordSecond = MutableLiveData<String>()

    fun validateInput(accountId: String, passwordFirst: String, passwordSecond: String): Boolean {
        return accountId.isNotBlank() && passwordFirst.isNotBlank() && passwordFirst == passwordSecond
    }

    fun register(userName: String, passwordFirst: String, passwordSecond: String) {
        kotlin.runCatching {
            registerUseCase.validateUsername(userName)
        }.onFailure { exception ->
            when(exception) {
                is IllegalArgumentException -> {
                    _registerUIState.value = _registerUIState.value?.copy(
                        userMessages = listOf(Message(MessageId.USERNAME_ERROR, R.string.error_username_required)))
                }
            }
        }

        kotlin.runCatching {
            registerUseCase.validatePassword(passwordFirst, passwordSecond)
        }.onFailure {
            when(it) {
                is IllegalArgumentException -> {
                    _registerUIState.value = _registerUIState.value?.copy(
                        userMessages = listOf(Message(MessageId.PWD_ONE_ERROR, R.string.error_password_cannot_be_empty)))
                }
                is PasswordMismatchException -> {
                    _registerUIState.value = _registerUIState.value?.copy(
                        userMessages = listOf(Message(MessageId.PWD_TWO_ERROR, R.string.error_passwords_does_not_match)))
                }
            }
        }
        val account = Account(userName)
        viewModelScope.launch { // todo move it to IO thread in repository
            try {
                accountRepository.createAccount(account)
                _registerUIState.value = RegisterUIState(isRegistered = true)
            } catch (e: SQLiteConstraintException) {
                _registerUIState.value = RegisterUIState(userMessages = listOf(
                    Message(MessageId.REGISTER_ERROR, R.string.error_username_already_exists)
                ))
            } catch (e: Exception) {
                _registerUIState.value = RegisterUIState(userMessages = listOf(
                    Message(MessageId.REGISTER_ERROR, R.string.error_cannot_register)
                ))
            }
        }
    }
}

data class RegisterUIState(
    val isRegistered: Boolean = false,
    val isLoading: Boolean = false,
    val userMessages: List<Message> = listOf(),
)

data class Message(
    val id: Long,
    val messageId: Int,
)

object MessageId {
    const val PWD_ONE_ERROR: Long = 1
    const val PWD_TWO_ERROR: Long = 2
    const val USERNAME_ERROR: Long = 3
    const val REGISTER_ERROR: Long = 4
}