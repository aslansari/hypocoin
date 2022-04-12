package com.aslansari.hypocoin.viewmodel.account

import com.aslansari.hypocoin.repository.model.Account

class AccountUIModel {
    var isLoading = false
    var isLoggedIn = false
    var account: Account? = null

    companion object {
        @JvmStatic
        fun idle(): AccountUIModel {
            // TODO create appropriate constructors
            val model = AccountUIModel()
            model.account = null
            model.isLoading = false
            model.isLoggedIn = false
            return model
        }
    }
}