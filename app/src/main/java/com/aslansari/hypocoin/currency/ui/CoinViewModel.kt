package com.aslansari.hypocoin.currency.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aslansari.hypocoin.currency.data.model.Currency
import com.aslansari.hypocoin.repository.CoinRepository
import com.aslansari.hypocoin.ui.DisplayTextUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * TODO get saved state from app component
 * TODO refresh currency list
 */
@HiltViewModel
class CoinViewModel @Inject constructor(
    private val coinRepository: CoinRepository
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _currencyList = MutableLiveData<List<CurrencyListItem>>()
    val currencyList: LiveData<List<CurrencyListItem>> = _currencyList

    val asyncCurrencyList: Observable<List<Currency>>
        get() = coinRepository.currencyList

    fun getCurrencyList() {
        viewModelScope.launch(Dispatchers.Main) {
            _loading.value = true
            val list = withContext(Dispatchers.IO) {
                val currencyList: MutableList<CurrencyListItem> = mutableListOf()
                coinRepository.getCurrency()
                    .map {
                        val priceDouble = it.metrics?.marketData?.priceUSD ?: 0.0
                        val price = (priceDouble * 100).toLong()
                        CurrencyListItem(
                            it.id,
                            it.name ?: "",
                            it.symbol ?: "",
                            DisplayTextUtil.Amount.getDollarAmountWithSign(price)
                        )
                    }
                    .asFlow()
                    .toList(currencyList)
                currencyList
            }
            _currencyList.value = list
            _loading.value = false
        }
    }
}