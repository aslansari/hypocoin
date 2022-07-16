package com.aslansari.hypocoin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aslansari.hypocoin.repository.CoinRepository
import com.aslansari.hypocoin.repository.model.Currency
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext

/**
 * TODO get saved state from app component
 * TODO refresh currency list
 */
class CoinViewModel(private val coinRepository: CoinRepository) : ViewModel() {
    private val id: String? = null
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _currencyList = MutableLiveData<List<Currency>>()
    val currencyList: LiveData<List<Currency>> = _currencyList

    val asyncCurrencyList: Observable<List<Currency>>
        get() = coinRepository.currencyList

    fun getCurrencyList() {
        viewModelScope.launch(Dispatchers.Main) {
            _loading.value = true
            val list = withContext(Dispatchers.IO) {
                val currencyList: MutableList<Currency> = mutableListOf()
                coinRepository.getCurrency()
                    .asFlow()
                    .toList(currencyList)
                currencyList
            }
            _currencyList.value = list
            _loading.value = false
        }
    }
}