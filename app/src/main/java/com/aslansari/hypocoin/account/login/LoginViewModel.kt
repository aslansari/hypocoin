package com.aslansari.hypocoin.account.login

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.viewmodel.login.LoginError
import com.aslansari.hypocoin.viewmodel.login.LoginResult
import com.aslansari.hypocoin.viewmodel.login.LoginUIModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel(
    private val loginUseCase: LoginUseCase
): ViewModel() {

    private var _loginUIState = MutableLiveData<LoginUIModel>()
    val loginUIState: LiveData<LoginUIModel> = _loginUIState

    fun onLoginClick(email: String, password: String) {
        Timber.d("Login clicked")
        _loginUIState.value = LoginUIModel.Loading
        viewModelScope.launch {
            loginUseCase.loginUser(email, password) { signInResult ->
                if (signInResult.isSuccess) {
                    _loginUIState.value = LoginUIModel.Result(LoginResult.LOGIN_SUCCESS)
                    _loginUIState.value = LoginUIModel.Idle
                } else {
                    _loginUIState.value = LoginUIModel.Error(signInResult.loginError)
                }
            }
            _loginUIState.value = LoginUIModel.Idle
        }
    }

    fun onEmailTextChange() {
        _loginUIState.value = LoginUIModel.Idle
    }

    fun onSubmitEmailClick(email: String) {
        _loginUIState.value = LoginUIModel.Loading
        viewModelScope.launch {
            if (loginUseCase.validateEmail(email)) {
                loginUseCase.checkEmail(email) {
                    if (it) {
                        _loginUIState.value = LoginUIModel.Result(LoginResult.EMAIL_VALID)
                        _loginUIState.value = LoginUIModel.Idle
                    } else {
                        _loginUIState.value = LoginUIModel.Error(LoginError.EMAIL_DOES_NOT_EXISTS)
                    }
                }
            } else {
                _loginUIState.value = LoginUIModel.Error(LoginError.EMAIL_INVALID)
            }
        }
    }

    fun onForgotPasswordClick(email: String) {
        Timber.d(email)
    }

    fun onGoogleLoginClick() {
        _loginUIState.value = LoginUIModel.Result(LoginResult.GOOGLE_LOGIN_REQUEST)
    }

    fun onGoogleSignInResult(activityResult: ActivityResult) {
        _loginUIState.value = LoginUIModel.Loading
        viewModelScope.launch {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                if (!account.email.isNullOrBlank()) {
                    loginUseCase.checkEmail(account.email!!) { exists ->
                        if (exists) {
                            loginUseCase.signInWithGoogle(account) { success ->
                                onSignInWithCredentialResult(success)
                            }
                        } else {
                            _loginUIState.value = LoginUIModel.RegisterWithGoogle(account)
                        }
                    }
                }
            } catch (e: ApiException) {
                Timber.e(e, "Google sign in failed")
                when (e.status) {
                    Status.RESULT_CANCELED -> {
                        _loginUIState.value = LoginUIModel.Error(LoginError.GOOGLE_LOGIN_CANCELED)
                    }
                    else -> {
                        _loginUIState.value = LoginUIModel.Error(LoginError.GENERAL)
                    }
                }
            }
        }
    }

    private fun onSignInWithCredentialResult(success: Boolean) {
        if (success) {
            _loginUIState.value = LoginUIModel.Result(LoginResult.LOGIN_SUCCESS)
        } else {
            _loginUIState.value = LoginUIModel.Error(LoginError.GENERAL)
        }
    }

    fun signInWithGoogle(
        activity: FragmentActivity,
        resultLauncher: ActivityResultLauncher<Intent>
    ) {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val signInClient = GoogleSignIn.getClient(activity, options)
        resultLauncher.launch(signInClient.signInIntent)
    }

    fun cancelGoogleSignIn() {
        _loginUIState.value = LoginUIModel.Idle
    }

    fun continueRegisterWithGoogle(account: GoogleSignInAccount) {
        loginUseCase.signInWithGoogle(account) {
            onSignInWithCredentialResult(it)
        }
    }
}
