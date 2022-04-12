package com.trader.note.view.trade

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.internal.LinkedTreeMap
import com.trader.note.api.Api
import com.trader.note.urls.symbolPriceUrl
import kotlinx.coroutines.launch


class AddTradeViewModel(private val api: Api) : ViewModel() {

    private val _searchSymbols: MutableLiveData<List<String>> by lazy {
        MutableLiveData()
    }
    val searchSymbols: LiveData<List<String>>
        get() = _searchSymbols


    private val _symbolPrice: MutableLiveData<String> by lazy {
        MutableLiveData()
    }
    val symbolPrice: LiveData<String>
        get() = _symbolPrice

    fun fetchSymbols() {
        viewModelScope.launch {
            val data = api.getSymbols()

            if (data == null) {
                _searchSymbols.postValue(emptyList())
            } else {
                _searchSymbols.postValue(data.coins.map {
                    it.id
                })
            }
        }
    }

    fun fetchPrice(id: String) {
        viewModelScope.launch {
            val price = api.getPriceOf(id)
            _symbolPrice.postValue(price?.toString() ?: "0.0")
        }
    }


}