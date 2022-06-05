package com.aslansari.hypocoin.register

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import com.aslansari.hypocoin.register.exception.PasswordMismatchException
import com.aslansari.hypocoin.repository.AccountRepository
import com.aslansari.hypocoin.repository.model.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import timber.log.Timber

class RegisterUseCase(
    private val accountRepository: AccountRepository,
    private val auth: FirebaseAuth?,
) {


    suspend fun register(account: Account, listener: (RegisterResult) -> Unit) {
        auth?.createUserWithEmailAndPassword(account.email, account.passwordPlaintext)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.d("createUserWithEmail:success")
                    val user = auth.currentUser
                    listener.invoke(RegisterResult(0, "register success", RegisterResultStatus.SUCCESS))
                    // update UI with user
                } else {
                    // If sign in fails, display a message to the user.
                    Timber.w("createUserWithEmail:failure - %s", it.exception?.message)
                    // updateUI(null)
                    when (it.exception) {
                        is FirebaseAuthWeakPasswordException -> {
                            // weak password
                            listener.invoke(RegisterResult(1, "weak password", RegisterResultStatus.WEAK_PASSWORD))
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            // invalid email
                            listener.invoke(RegisterResult(1, "weak password", RegisterResultStatus.INVALID_CREDENTIALS))
                        }
                        is FirebaseAuthUserCollisionException -> {
                            // user already exists
                            listener.invoke(RegisterResult(1, "weak password", RegisterResultStatus.USER_ALREADY_EXISTS))
                        }
                        else -> {
                            // general error
                            listener.invoke(RegisterResult(1, "weak password", RegisterResultStatus.REGISTER_ERROR))
                        }
                    }
                }
            }
        // todo maybe listen for cancel
        // accountRepository.createAccount(account)
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
data class RegisterResult(
    val errorCode: Int,
    val errorMessage: String,
    val status: RegisterResultStatus,
)