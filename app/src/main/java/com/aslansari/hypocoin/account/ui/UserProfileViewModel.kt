package com.aslansari.hypocoin.account.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aslansari.hypocoin.account.data.AccountRepository
import com.aslansari.hypocoin.account.data.SendVerificationEmailTask
import com.aslansari.hypocoin.account.domain.NetWorthUseCase
import com.aslansari.hypocoin.account.domain.WalletInfoUseCase
import com.aslansari.hypocoin.app.util.AnalyticsReporter
import com.aslansari.hypocoin.currency.domain.CurrencyPriceUseCase
import com.aslansari.hypocoin.ui.DisplayTextUtil
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.math.absoluteValue

class UserProfileViewModel(
    private val walletInfoUseCase: WalletInfoUseCase,
    private val currencyPriceUseCase: CurrencyPriceUseCase,
    private val netWorthUseCase: NetWorthUseCase,
    private val accountRepository: AccountRepository,
    private val analyticsReporter: AnalyticsReporter,
) : ViewModel() {

    val walletUIState = combine(walletInfoUseCase.wallet(), netWorthUseCase.netWorthFlow, netWorthUseCase.roiDataFlow) {
        userWallet, netWorth, roiData,  ->
        if (userWallet == null) {
            UserWalletUIModel.Error
        } else {
            val assetListItems = userWallet.userAssets.map { item ->
                val priceUsd = currencyPriceUseCase.getCurrencyPrice(item.id)
                val priceUsdText = DisplayTextUtil.Amount.getDollarAmount(priceUsd)
                AssetListItem(item.id, DisplayTextUtil.Amount.getCurrencyFormat(item.amount), item.name, item.symbol, priceUsdText)
            }
            val roiType = if (roiData.percentChangeLast1Week > 0) RoiType.GAIN else RoiType.LOSS
            val netWorthModel = NetWorthUIModel(netWorth, RoiChip(roiType, roiData.percentChangeLast1Week.absoluteValue * 100))
            UserWalletUIModel.Result(userWallet.user, assetListItems, netWorthModel)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        UserWalletUIModel.Loading
    )

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
