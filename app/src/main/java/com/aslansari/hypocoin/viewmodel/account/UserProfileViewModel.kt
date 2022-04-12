package com.aslansari.hypocoin.viewmodel.account

import com.aslansari.hypocoin.repository.AccountRepository
import androidx.lifecycle.ViewModel
import com.aslansari.hypocoin.repository.model.Account
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import com.aslansari.hypocoin.viewmodel.account.UserProfileAction
import io.reactivex.rxjava3.core.Single
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class UserProfileViewModel(private val accountRepository: AccountRepository) : ViewModel() {

    companion object {
        @JvmField
        val AMOUNT_FORMAT: DecimalFormat =
            DecimalFormat("###,##0.00", DecimalFormatSymbols.getInstance(Locale.US))
    }

    val actionPublishSubject: PublishSubject<UserProfileAction> = PublishSubject.create()
    val isLoggedIn = false
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

}