package com.aslansari.hypocoin.account.data

import com.aslansari.hypocoin.account.login.data.LoginError
import com.aslansari.hypocoin.account.register.data.RegisterResult
import com.aslansari.hypocoin.account.register.data.RegisterResultStatus
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AccountRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val accountDAO: AccountDAO,
    private val auth: FirebaseAuth,
    val database: DatabaseReference,
) {

    private val usersReference = database.child(DatabaseModel.USERS)

    suspend fun getAccountWithInfo(): UserResult {
        return withContext(ioDispatcher) {
            getAccount()
        }
    }

    private suspend fun getAccount(): UserResult = suspendCoroutine { continuation ->
        if (getCurrentUser() != null) {
            val user = getCurrentUser()!!
            val hasPassword = user.providerData
                .map { userInfo ->  userInfo.providerId }
                .contains(EmailAuthProvider.PROVIDER_ID)
            usersReference.child(user.uid).child(DatabaseModel.BALANCE).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val balance = task.result.value ?: 0L
                    continuation.resume(
                        UserResult.User(
                            uid = auth.currentUser?.uid ?: "",
                            email = auth.currentUser?.email ?: "",
                            displayName = auth.currentUser?.displayName ?: "",
                            balance = balance as Long,
                            createdAt = user.metadata?.creationTimestamp ?: 0L,
                            lastLogin = user.metadata?.lastSignInTimestamp ?: 0L,
                            phoneNumber = user.phoneNumber ?: "",
                            multiFactorMethods = getMultiFactorList(user.multiFactor.enrolledFactors),
                            isEmailVerified = user.isEmailVerified,
                            hasPassword = hasPassword
                        )
                    )
                } else {
                    continuation.resume(UserResult.Error)
                }
            }.addOnCanceledListener {
                continuation.resume(UserResult.Error)
            }
        } else {
            continuation.resume(UserResult.Error)
        }
    }

    private fun getMultiFactorList(enrolledFactors: MutableList<MultiFactorInfo>): List<String> {
        return enrolledFactors.map { it.displayName ?: "default" }
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
        database.child(DatabaseModel.USERS).child(user.uid).setValue(
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
                listener(
                    RegisterResult(
                    0,
                    "register success",
                    RegisterResultStatus.SUCCESS
                )
                )
                // update UI with user
            } else {
                // If sign in fails, display a message to the user.
                Timber.w("createUserWithEmail:failure")
                Timber.v(it.exception)
                // updateUI(null)
                when (it.exception) {
                    is FirebaseAuthWeakPasswordException -> {
                        // weak password
                        listener(
                            RegisterResult(
                            1,
                            "weak password",
                            RegisterResultStatus.WEAK_PASSWORD
                        )
                        )
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        // invalid email
                        listener(
                            RegisterResult(
                            1,
                            "weak password",
                            RegisterResultStatus.INVALID_CREDENTIALS
                        )
                        )
                    }
                    is FirebaseAuthUserCollisionException -> {
                        // user already exists
                        listener(
                            RegisterResult(
                            1,
                            "weak password",
                            RegisterResultStatus.USER_ALREADY_EXISTS
                        )
                        )
                    }
                    else -> {
                        // general error
                        listener(
                            RegisterResult(
                            1,
                            "weak password",
                            RegisterResultStatus.REGISTER_ERROR
                        )
                        )
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

    fun logout(completeListener: () -> Unit) {
        auth.signOut()
        completeListener()
    }

    fun sendVerificationEmail(completeListener: (SendVerificationEmailTask) -> Unit) {
        auth.currentUser?.let { user ->
            user.sendEmailVerification()
                .addOnCompleteListener {
                    completeListener(
                        SendVerificationEmailTask(
                        isSuccessful = it.isSuccessful,
                    )
                    )
                }
        }
    }

    fun updateDisplayName(displayName: String, completeListener: (Boolean) -> Unit) {
        auth.currentUser?.let {
            val changeRequestBuilder = UserProfileChangeRequest.Builder()
            changeRequestBuilder.displayName = displayName
            it.updateProfile(changeRequestBuilder.build()).addOnCompleteListener { task ->
                completeListener(task.isSuccessful)
            }
        }
    }

    fun updateEmail(email: String, completeListener: (Boolean) -> Unit) {
        auth.currentUser?.let {
            it.updateEmail(email).addOnCompleteListener { task ->
                completeListener(task.isSuccessful)
            }
        }
    }
}