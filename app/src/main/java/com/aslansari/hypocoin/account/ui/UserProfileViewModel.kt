package com.aslansari.hypocoin.account.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aslansari.hypocoin.account.data.AccountRepository
import com.aslansari.hypocoin.account.data.SendVerificationEmailTask
import com.aslansari.hypocoin.account.domain.WalletInfoUseCase
import com.aslansari.hypocoin.app.util.AnalyticsReporter
import com.aslansari.hypocoin.currency.domain.CurrencyPriceUseCase
import com.aslansari.hypocoin.ui.DisplayTextUtil
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val walletInfoUseCase: WalletInfoUseCase,
    private val currencyPriceUseCase: CurrencyPriceUseCase,
    private val accountRepository: AccountRepository,
    private val analyticsReporter: AnalyticsReporter,
) : ViewModel() {

    private val _userInfoUIModel = MutableLiveData<UserWalletUIModel>()
    val userInfoUIModelLiveData = _userInfoUIModel as LiveData<UserWalletUIModel>

    fun getUserInfo() {
        viewModelScope.launch {
            _userInfoUIModel.value = UserWalletUIModel.Loading
            val userWallet = walletInfoUseCase.getUserWallet()
            if (userWallet == null) {
                _userInfoUIModel.value = UserWalletUIModel.Error
            } else {
                val assetListItems = userWallet.userAssets.map { item ->
                    val priceUsd = currencyPriceUseCase.getCurrencyPrice(item.id)
                    val priceUsdText = DisplayTextUtil.Amount.getDollarAmount(priceUsd)
                    AssetListItem(item.id, DisplayTextUtil.Amount.getCurrencyFormat(item.amount), item.name, item.symbol, priceUsdText)
                }
                _userInfoUIModel.value = UserWalletUIModel.Result(userWallet.user, assetListItems)
            }
        }
    }

    fun isLoggedIn(): Boolean {
        return accountRepository.isLoggedIn()
    }

    fun logout(completeListener: () -> Unit) {
        accountRepository.logout(completeListener)
    }

    fun sendVerificationEmail(completeListener: (SendVerificationEmailTask) -> Unit) {
        accountRepository.sendVerificationEmail(completeListener)
    }

    fun updateDisplayNumber(displayName: String, completeListener: (Boolean) -> Unit) {
        accountRepository.updateDisplayName(displayName, completeListener)
    }

    fun updateEmail(email: String, completeListener: (Boolean) -> Unit) {
        accountRepository.updateEmail(email, completeListener)
    }
}
