package com.aslansari.hypocoin.account.data

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.aslansari.hypocoin.account.data.dto.User
import com.aslansari.hypocoin.account.login.data.LoginError
import com.aslansari.hypocoin.account.register.data.RegisterResult
import com.aslansari.hypocoin.account.register.data.RegisterResultStatus
import com.google.firebase.auth.*
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import timber.log.Timber

class AccountRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val accountDAO: AccountDAO,
    private val auth: FirebaseAuth,
    private val database: DatabaseReference,
) {

    private val usersReference = database.child(DatabaseModel.USERS)
    private var listener: ValueEventListener? = null
    private var userListener: ((FirebaseUser?) -> Unit)? = null

    private val userLoginFlow = callbackFlow {
        if (getCurrentUser() != null) {
            trySend(getCurrentUser())
        }
        userListener = {
            trySend(it)
        }
        awaitClose { userListener = null }
    }.shareIn(
        ProcessLifecycleOwner.get().lifecycleScope,
        SharingStarted.WhileSubscribed(),
        1
    )

    val accountFlow = userLoginFlow.flatMapLatest {
        accountFlowWithUser(it)
    }

    private fun accountFlowWithUser(user: FirebaseUser?) = callbackFlow {
        trySend(UserResult.Loading)
        if (user != null) {
            val hasPassword = user.providerData
                .map { userInfo -> userInfo.providerId }
                .contains(EmailAuthProvider.PROVIDER_ID)
            listener =
                usersReference.child(user.uid).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val balance = snapshot.getValue(User::class.java)?.balance ?: 0L
                        trySend(
                            UserResult.User(
                                uid = user.uid,
                                email = user.email ?: "",
                                displayName = user.displayName ?: "",
                                balance = balance,
                                createdAt = user.metadata?.creationTimestamp ?: 0L,
                                lastLogin = user.metadata?.lastSignInTimestamp ?: 0L,
                                phoneNumber = user.phoneNumber ?: "",
                                multiFactorMethods = getMultiFactorList(user.multiFactor.enrolledFactors),
                                isEmailVerified = user.isEmailVerified,
                                hasPassword = hasPassword
                            )
                        )
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Timber.e("user fetch error, code=${error.code}")
                    }
                })
        } else {
            send(UserResult.NotLogin)
        }

        awaitClose {
            listener?.let { usersReference.child("").removeEventListener(it) }
        }
    }

    fun depositBalance(balance: Long, completeListener: (Boolean) -> Unit) {
        if (getCurrentUser() != null) {
            val user = getCurrentUser()!!
            val updates: MutableMap<String, Any> = hashMapOf(
                "users/${user.uid}/balance" to ServerValue.increment(balance)
            )
            database.updateChildren(updates).addOnCompleteListener {
                completeListener(it.isSuccessful)
            }
        }
    }

    fun withdrawBalance(balance: Long, completeListener: (Boolean) -> Unit) {
        if (getCurrentUser() != null) {
            val user = getCurrentUser()!!
            val updates: MutableMap<String, Any> = hashMapOf(
                "users/${user.uid}/balance" to ServerValue.increment(-balance)
            )
            database.updateChildren(updates).addOnCompleteListener {
                completeListener(it.isSuccessful)
            }
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
            User(id = user.uid, balance = 0L)
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
                Timber.d("UserInfo: current user is null : ${auth.currentUser == null}")
                auth.currentUser?.let { user ->
                    createUserOnDB(user)
                    userListener?.invoke(user)
                }
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
                    userListener?.invoke(it.result.user)
                    completeListener.invoke(
                        LoginResult(
                            isSuccess = it.isSuccessful,
                            isCanceled = it.isCanceled,
                        )
                    )
                } else {
                    val loginError = when (it.exception) {
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

    fun signInWithGoogleCredential(
        credential: AuthCredential,
        completeListener: (Boolean) -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                completeListener.invoke(it.isSuccessful)
                if (it.isSuccessful) {
                    userListener?.invoke(it.result.user)
                }
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
                        userListener?.invoke(user)
                    }
                }
                completeListener(it.isSuccessful)
            }
    }

    fun logout(completeListener: () -> Unit) {
        auth.signOut()
        userListener?.invoke(null)
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