package com.aslansari.hypocoin.account.login

import androidx.core.util.PatternsCompat
import com.aslansari.hypocoin.repository.AccountRepository

class LoginUseCase(
    private val accountRepository: AccountRepository
) {

    fun checkEmail(email: String, listener: (Boolean) -> Unit) {
        return accountRepository.isAccountExistsByEmail(email, listener)
    }

    fun validateEmail(email: String): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun loginUser(email: String, password: String) {
        // TODO login existing user
    }
}