package com.aslansari.hypocoin.register

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.repository.model.Account
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

/**
 * Todo error handling
 */
class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _registerUIState = MutableLiveData(RegisterUIState())
    val registerUIState: LiveData<RegisterUIState> = _registerUIState

    private val _registerResultUIState = MutableLiveData(RegisterResultUIState())
    val registerResultUIStateLiveData: LiveData<RegisterResultUIState> = _registerResultUIState

    private val _passwordInput = MutableLiveData("")
    private val _passwordConfirmInput = MutableLiveData("")

    private val flowPasswordInput = _passwordInput.asFlow().debounce(500)
    private val flowPasswordConfirmInput = _passwordConfirmInput.asFlow().debounce(500)
    private var isPasswordEntered = false
    private var isPasswordConfirmEntered = false
    private var registerResultUIState = RegisterResultUIState()
    private var registrationData = RegistrationData()

    fun onTextChange() {
        viewModelScope.launch {
            _registerUIState.value = defaultRegisterUIState()
        }
    }

    fun onSubmitEmail(email: String?) {
        viewModelScope.launch {
            _registerUIState.value = (RegisterUIState(loading = true, buttonEnabled = false))
            if (email.isNullOrBlank()) {
                _registerUIState.value = RegisterUIState(error = RegisterStatus.INPUT_EMPTY)
            } else {
                val isValid = registerUseCase.validateEmail(email)
                if (isValid) {
                    val isExists = registerUseCase.checkEmail(email)
                    _registerUIState.value = if (isExists) {
                        RegisterUIState(error = RegisterStatus.USER_ALREADY_EXISTS,
                            buttonEnabled = false)
                    } else {
                        RegisterUIState(
                            onSubmit = RegistrationData(email, null)
                        )
                    }
                } else {
                    _registerUIState.value =
                        RegisterUIState(error = RegisterStatus.INPUT_FORMAT_WRONG)
                }
            }
        }
    }

    fun registerWithGoogleButtonClick() {
        _registerUIState.value = RegisterUIState(error = RegisterStatus.SIGN_IN_WITH_GOOGLE)
    }

    fun registerWithGoogle(activity: FragmentActivity, resultLauncher: ActivityResultLauncher<Intent>) {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val signInClient = GoogleSignIn.getClient(activity, options)

        resultLauncher.launch(signInClient.signInIntent)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // todo publish success data
                } else {
                    // todo publish error
                }
            }
    }

    fun validateInput() =
        combine(flowPasswordInput, flowPasswordConfirmInput) { password, passwordConfirm ->
            if (!isPasswordEntered && password.isNotEmpty()) {
                isPasswordEntered = true
            }
            if (!isPasswordConfirmEntered && passwordConfirm.isNotEmpty()) {
                isPasswordConfirmEntered = true
            }

            if (password.isNullOrBlank().not()) {
                if (isPasswordConfirmEntered) {
                    if (passwordConfirm.isNullOrBlank().not()) {
                        if (password == passwordConfirm) {
                            registrationData.passwordUnencrypted = password
                            registerResultUIState = RegisterResultUIState()
                            registerResultUIState
                        } else {
                            // password does not match
                            registerResultUIState =
                                RegisterResultUIState(error = RegisterResultStatus.DOES_NOT_MATCH)
                            registerResultUIState
                        }
                    } else {
                        // "Please confirm your password"
                        registerResultUIState =
                            RegisterResultUIState(error = RegisterResultStatus.CONFIRM_YOUR_PASSWORD)
                        registerResultUIState
                    }
                } else {
                    // password is entered but confirm not yet entered
                    registerResultUIState = RegisterResultUIState()
                    registerResultUIState
                }
            } else {
                // "Password should not be empty"
                registerResultUIState =
                    RegisterResultUIState(error = RegisterResultStatus.SHOULD_NOT_BE_EMPTY)
                registerResultUIState
            }
        }.filter { isPasswordEntered }

    fun onPasswordInputChange(input: CharSequence) {
        _passwordInput.value = input.toString()
    }

    fun onPasswordConfirmInputChange(input: CharSequence) {
        _passwordConfirmInput.value = input.toString()
    }

    fun registerWithEmail() {
        viewModelScope.launch {
            val id = UUID.randomUUID().toString()
            val account = Account(id, registrationData.email ?: "")
            account.passwordPlaintext = registrationData.passwordUnencrypted.toString()
            registerUseCase.register(account) {
                _registerResultUIState.value = RegisterResultUIState(error = it.status)
            }
        }
    }

    fun setEmail(email: String?) {
        registrationData.email = email
    }

    fun onGoogleSignInResult(activityResult: ActivityResult) {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Timber.e(e, "Google sign in failed")
            when (e.status) {
                Status.RESULT_CANCELED -> {}
                else -> {}
            }
        }
    }
}

data class RegisterResultUIState(
    val loading: Boolean = false,
    val error: RegisterResultStatus = RegisterResultStatus.NO_ERROR,
    val buttonEnabled: Boolean = true,
)

enum class RegisterResultStatus {
    SUCCESS,
    NO_ERROR,
    NOT_VALID,
    DOES_NOT_MATCH,
    SHOULD_NOT_BE_EMPTY,
    CONFIRM_YOUR_PASSWORD,
    WEAK_PASSWORD,
    INVALID_CREDENTIALS,
    USER_ALREADY_EXISTS,
    REGISTER_ERROR,
}

data class RegisterUIState(
    val loading: Boolean = false,
    val error: RegisterStatus = RegisterStatus.NO_ERROR,
    val buttonEnabled: Boolean = true,
    val onSubmit: RegistrationData? = null,
)

fun defaultRegisterUIState() = RegisterUIState()

enum class RegisterStatus {
    NO_ERROR,
    USER_ALREADY_EXISTS,
    INPUT_FORMAT_WRONG,
    INPUT_EMPTY,
    SIGN_IN_WITH_GOOGLE,
}