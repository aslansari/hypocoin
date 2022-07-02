package com.aslansari.hypocoin.repository

import com.aslansari.hypocoin.register.RegisterResult
import com.aslansari.hypocoin.register.RegisterResultStatus
import com.aslansari.hypocoin.repository.model.Account
import com.aslansari.hypocoin.repository.model.AccountDAO
import com.aslansari.hypocoin.viewmodel.login.LoginError
import com.google.firebase.auth.*
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class AccountRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val accountDAO: AccountDAO,
    private val auth: FirebaseAuth,
) {

    fun getAccount(id: String): Single<Account> {
        return accountDAO.getAccount(id)
    }

    fun isAccountExistsByEmail(email: String, listener: (Boolean) -> Unit) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    listener(it.result.signInMethods?.isNotEmpty() == true)
                }
            }
    }

    fun isLoggedIn(): Boolean {
        return false
    }

    fun register(account: Account, listener: (RegisterResult) -> Unit) {
        val task = auth.createUserWithEmailAndPassword(account.email, account.passwordPlaintext)
        task.addOnCompleteListener {
            if (it.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Timber.d("createUserWithEmail:success")
                val user = auth.currentUser
                listener(RegisterResult(
                    0,
                    "register success",
                    RegisterResultStatus.SUCCESS
                ))
                // update UI with user
            } else {
                // If sign in fails, display a message to the user.
                Timber.w("createUserWithEmail:failure - %s", it.exception?.message)
                // updateUI(null)
                when (it.exception) {
                    is FirebaseAuthWeakPasswordException -> {
                        // weak password
                        listener(RegisterResult(
                            1,
                            "weak password",
                            RegisterResultStatus.WEAK_PASSWORD
                        ))
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        // invalid email
                        listener(RegisterResult(
                            1,
                            "weak password",
                            RegisterResultStatus.INVALID_CREDENTIALS
                        ))
                    }
                    is FirebaseAuthUserCollisionException -> {
                        // user already exists
                        listener(RegisterResult(
                            1,
                            "weak password",
                            RegisterResultStatus.USER_ALREADY_EXISTS
                        ))
                    }
                    else -> {
                        // general error
                        listener(RegisterResult(
                            1,
                            "weak password",
                            RegisterResultStatus.REGISTER_ERROR
                        ))
                    }
                }
            }
        }
    }

    fun signInWithEmail(email: String, password: String, completeListener: (LoginResult) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    completeListener.invoke(
                        LoginResult(
                            isSuccess = it.isSuccessful,
                            isCanceled = it.isCanceled,
                        )
                    )
                } else {
                    val loginError = when(it.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            LoginError.PASSWORD_INCORRECT
                        }
                        else -> {
                            LoginError.NONE
                        }
                    }
                    completeListener.invoke(
                        LoginResult(
                            isSuccess = it.isSuccessful,
                            isCanceled = it.isCanceled,
                            loginError = loginError,
                        )
                    )
                }
            }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun signInWithGoogleCredential(credential: AuthCredential, completeListener: (Boolean) -> Unit) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                completeListener.invoke(it.isSuccessful)
            }
    }

    fun forgotPassword(email: String, completeListener: (Boolean) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                completeListener.invoke(it.isSuccessful)
            }
    }
}