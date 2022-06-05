package com.aslansari.hypocoin.viewmodel.account

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aslansari.hypocoin.repository.AccountRepository
import com.aslansari.hypocoin.repository.model.Account
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class UserProfileViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {

    data class LoginUIState(
        val status: Int,
    )

    private val _loginScreenLiveData = MutableLiveData(LoginUIState(0))
    val loginScreenLiveData = _loginScreenLiveData

    companion object {
        @JvmField
        val AMOUNT_FORMAT: DecimalFormat =
            DecimalFormat("###,##0.00", DecimalFormatSymbols.getInstance(Locale.US))
    }

    val actionPublishSubject: PublishSubject<UserProfileAction> = PublishSubject.create()
    var id: String? = null
    fun login() {
        actionPublishSubject.onNext(UserProfileAction.LOGIN)
    }

    fun registerRequest() {
        actionPublishSubject.onNext(UserProfileAction.REGISTER_REQUEST)
    }

    fun register() {
        actionPublishSubject.onNext(UserProfileAction.REGISTER)
    }

    val account: Single<Account>
        get() = accountRepository.getAccount(id!!)

    fun isLoggedIn(): Boolean {
        return accountRepository.isLoggedIn()
    }
}