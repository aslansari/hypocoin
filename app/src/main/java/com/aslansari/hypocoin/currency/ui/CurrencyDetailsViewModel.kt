package com.aslansari.hypocoin.currency.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aslansari.hypocoin.account.ui.RoiChip
import com.aslansari.hypocoin.account.ui.RoiType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyDetailsViewModel @Inject constructor(
) : ViewModel() {

    private val _currencyDetailsState = MutableLiveData<CurrencyDetailsState>()
    val currencyDetailsState = _currencyDetailsState as LiveData<CurrencyDetailsState>

    fun init(id: String) {
        viewModelScope.launch {
            _currencyDetailsState.value = CurrencyDetailsState.Loading
            delay(3000)
            _currencyDetailsState.value = CurrencyDetailsState.Result(
                "BTC",
                14523441,
                0.00001231,
                RoiChip(RoiType.GAIN, 1.2),
                CurrencyDetailsState.AllTimeHigh(
                    14523441,
                    "21 July 2021",
                    2,
                    31.12
                )
            )
        }
    }

    fun sellCurrency(id: String) {
        // todo check if have enough assets
    }

    fun buyCurrency(id: String) {
        // todo check if user have more than 0 USD
    }

    fun addToFavourite(id: String) {

    }

    fun removeFromFavourite(id: String) {

    }
}