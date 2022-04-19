package com.aslansari.hypocoin.account.login

import androidx.lifecycle.ViewModel
import timber.log.Timber

class LoginViewModel: ViewModel() {

    private var registerClickListener: (() -> Unit)? = null
    fun registerClicked() {
        registerClickListener?.invoke()
    }

    fun onRegisterClicked(registerClickListener: () -> Unit) {
        this.registerClickListener = registerClickListener
    }

    fun loginClicked(id: String) {
        Timber.i("login clicked with id:$id")
    }
}
