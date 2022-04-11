package com.aslansari.hypocoin.app

import android.content.Context
import com.aslansari.hypocoin.register.Register
import com.aslansari.hypocoin.viewmodel.CoinViewModel
import com.aslansari.hypocoin.viewmodel.account.UserProfileViewModel
import com.aslansari.hypocoin.register.RegisterViewModel
import com.aslansari.hypocoin.repository.CoinDatabase
import com.aslansari.hypocoin.repository.restapi.CoinAPI
import com.aslansari.hypocoin.repository.CoinRepository
import com.aslansari.hypocoin.repository.AccountRepository
import com.aslansari.hypocoin.repository.restapi.CoinServiceGenerator

class AppContainer(context: Context) {
    @JvmField
    val coinViewModel: CoinViewModel
    @JvmField
    val userProfileViewModel: UserProfileViewModel
    @JvmField
    val registerViewModel: RegisterViewModel
    private val coinDatabase: CoinDatabase
    private val coinAPI: CoinAPI
    private val coinRepository: CoinRepository
    private val accountRepository: AccountRepository

    init {
        coinDatabase = CoinDatabase.getDatabase(context)
        coinAPI = CoinServiceGenerator.createService(CoinAPI::class.java, CoinAPI.BASE_URL)
        coinRepository = CoinRepository(coinDatabase, coinAPI)
        accountRepository = AccountRepository(coinDatabase.accountDAO())
        coinViewModel = CoinViewModel(coinRepository)
        userProfileViewModel = UserProfileViewModel(accountRepository)
        val register = Register()
        registerViewModel = RegisterViewModel(register, accountRepository)
    }
}