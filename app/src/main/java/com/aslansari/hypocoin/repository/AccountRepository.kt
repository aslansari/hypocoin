package com.aslansari.hypocoin.repository

import com.aslansari.hypocoin.register.RegisterResult
import com.aslansari.hypocoin.register.RegisterResultStatus
import com.aslansari.hypocoin.repository.model.Account
import com.aslansari.hypocoin.repository.model.AccountDAO
import com.aslansari.hypocoin.viewmodel.login.LoginError
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

sealed class UserResult {
    object Error: UserResult()
    data class User(
        val uid: String,
        val email: String,
        val displayName: String,
        val balance: Long,
    ): UserResult()
}

class AccountRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val accountDAO: AccountDAO,
    private val auth: FirebaseAuth,
    val database: DatabaseReference,
) {

    suspend fun getAccountWithInfo(completeListener: (UserResult) -> Unit) {
        withContext(ioDispatcher) {
            auth.currentUser?.let {
                database.child("users").child(it.uid).child("balance").get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val balance = task.result.value ?: 0L
                        completeListener(UserResult.User(
                            uid = auth.currentUser?.uid ?: "",
                            email = auth.currentUser?.email ?: "",
                            displayName = auth.currentUser?.displayName ?: "",
                            balance = balance as Long
                        ))
                    } else {
                        completeListener(UserResult.Error)
                    }
                }
            }
        }
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
        return auth.currentUser != null
    }

    private fun createUserOnDB(user: FirebaseUser) {
        database.child("users").child(user.uid).setValue(
            UserResult.User(
                uid = user.uid,
                email = user.email ?: "",
                displayName = user.displayName ?: "",
                balance = 0L,
            )
        ).addOnCompleteListener {
            Timber.d("create user on realtime db ${it.isSuccessful}")
        }
    }

    fun register(account: Account, listener: (RegisterResult) -> Unit) {
        val task = auth.createUserWithEmailAndPassword(account.email, account.passwordPlaintext)
        task.addOnCompleteListener {
            if (it.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Timber.d("createUserWithEmail:success")
                auth.currentUser?.let { user -> createUserOnDB(user) }
                listener(RegisterResult(
                    0,
                    "register success",
                    RegisterResultStatus.SUCCESS
                ))
                // update UI with user
            } else {
                // If sign in fails, display a message to the user.
                Timber.w("createUserWithEmail:failure")
                Timber.v(it.exception)
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

    private fun getCurrentUser(): FirebaseUser? {
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

    fun registerWithGoogle(credential: AuthCredential, completeListener: (Boolean) -> Unit) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    auth.currentUser?.let { user ->
                        createUserOnDB(user)
                    }
                }
                completeListener(it.isSuccessful)
            }
    }
}