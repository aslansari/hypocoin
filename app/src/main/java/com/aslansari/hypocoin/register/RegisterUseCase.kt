package com.aslansari.hypocoin.register

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import com.aslansari.hypocoin.register.exception.PasswordMismatchException
import com.aslansari.hypocoin.repository.AccountRepository
import com.aslansari.hypocoin.repository.model.Account

class RegisterUseCase(private val accountRepository: AccountRepository) {

    suspend fun register(account: Account) {
        accountRepository.createAccount(account)
    }

    @Throws(IllegalArgumentException::class)
    fun validateUsername(userName: String?): Boolean {
        require(userName.isNullOrBlank().not()) { "username cannot be empty" }
        return true
    }

    @Throws(IllegalArgumentException::class, PasswordMismatchException::class)
    fun validatePassword(password: String?, passwordRepeat: String?) {
        require(!(password == null || password.isEmpty())) { "password field cannot be empty" }
        require(!(passwordRepeat == null || passwordRepeat.isEmpty())) { "password field cannot be empty" }
        if (password != passwordRepeat) {
            throw PasswordMismatchException("password fields does not match")
        }
    }

    suspend fun checkEmail(email: String): Boolean {
        return accountRepository.isAccountExistsByEmail(email)
    }

    fun validateEmail(email: String): Boolean {
        return EMAIL_ADDRESS.matcher(email).matches()
    }
}