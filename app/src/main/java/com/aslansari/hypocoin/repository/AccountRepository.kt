package com.aslansari.hypocoin.repository

import com.aslansari.hypocoin.register.RegisterResult
import com.aslansari.hypocoin.register.RegisterResultStatus
import com.aslansari.hypocoin.repository.model.Account
import com.aslansari.hypocoin.repository.model.AccountDAO
import com.google.firebase.auth.*
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

class AccountRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val accountDAO: AccountDAO,
    private val auth: FirebaseAuth,
) {

    fun getAccount(id: String): Single<Account> {
        return accountDAO.getAccount(id)
    }

    fun isAccountExistsByEmail(email: String) = callbackFlow {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    trySend(it.result.signInMethods?.isNotEmpty() == true)
                }
            }
        awaitClose {  }
    }

    fun isLoggedIn(): Boolean {
        return false
    }

    fun register(account: Account) = callbackFlow {
        val task = auth.createUserWithEmailAndPassword(account.email, account.passwordPlaintext)
        task.addOnCompleteListener {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.d("createUserWithEmail:success")
                    val user = auth.currentUser
                    trySend(RegisterResult(
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
                            trySend(RegisterResult(
                                1,
                                "weak password",
                                RegisterResultStatus.WEAK_PASSWORD
                            ))
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            // invalid email
                            trySend(RegisterResult(
                                1,
                                "weak password",
                                RegisterResultStatus.INVALID_CREDENTIALS
                            ))
                        }
                        is FirebaseAuthUserCollisionException -> {
                            // user already exists
                            trySend(RegisterResult(
                                1,
                                "weak password",
                                RegisterResultStatus.USER_ALREADY_EXISTS
                            ))
                        }
                        else -> {
                            // general error
                            trySend(RegisterResult(
                                1,
                                "weak password",
                                RegisterResultStatus.REGISTER_ERROR
                            ))
                        }
                    }
                }
            }
        awaitClose {  }
    }

    fun signInWithGoogleCredential(credential: AuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // todo publish success data
                } else {
                    // todo publish error
                }
            }
    }
}