package com.aslansari.hypocoin.account.login.domain

import androidx.core.util.PatternsCompat
import com.aslansari.hypocoin.account.data.AccountRepository
import com.aslansari.hypocoin.account.data.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.GoogleAuthProvider

class LoginUseCase(
    private val accountRepository: AccountRepository
) {

    fun checkEmail(email: String, listener: (Boolean) -> Unit) {
        return accountRepository.isAccountExistsByEmail(email, listener)
    }

    fun validateEmail(email: String): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun loginUser(email: String, password: String, completeListener: (LoginResult) -> Unit) {
        accountRepository.signInWithEmail(email, password, completeListener)
    }

    fun signInWithGoogle(account: GoogleSignInAccount, completeListener: (Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        accountRepository.signInWithGoogleCredential(credential, completeListener)
    }

    fun registerWithGoogle(account: GoogleSignInAccount, completeListener: (Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        accountRepository.registerWithGoogle(credential, completeListener)
    }

    fun forgotPassword(email: String, completeListener: (Boolean) -> Unit) {
        accountRepository.forgotPassword(email, completeListener)
    }
}