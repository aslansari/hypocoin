package com.aslansari.hypocoin.register

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import com.aslansari.hypocoin.register.exception.PasswordMismatchException
import com.aslansari.hypocoin.repository.AccountRepository
import com.aslansari.hypocoin.repository.model.Account
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.GoogleAuthProvider

class RegisterUseCase(
    private val accountRepository: AccountRepository,
) {

    fun register(account: Account, listener: (RegisterResult) -> Unit) {
        accountRepository.register(account, listener)
    }

    fun authRegisterWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        accountRepository.signInWithGoogleCredential(credential) { isSuccess -> }
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

    fun checkEmail(email: String, listener: (Boolean) -> Unit) {
        return accountRepository.isAccountExistsByEmail(email, listener)
    }

    fun validateEmail(email: String): Boolean {
        return EMAIL_ADDRESS.matcher(email).matches()
    }
}

data class RegisterResult(
    val errorCode: Int,
    val errorMessage: String,
    val status: RegisterResultStatus,
)