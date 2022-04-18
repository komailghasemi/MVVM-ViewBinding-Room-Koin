package com.trader.note.view.main

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.trader.note.model.repo.TradingPeriodRepository
import com.trader.note.model.repo.TradingPeriodModel
import kotlinx.coroutines.flow.map


class MainViewModel(private val tradingPeriodRepository: TradingPeriodRepository) : ViewModel() {

    //Models
    fun getList() = Pager(
        config = PagingConfig(
            pageSize = 25,
            enablePlaceholders = true,
            maxSize = 100
        )
    ) {
        tradingPeriodRepository.getAll()
    }.flow.cachedIn(viewModelScope).asLiveData()
    //Models


}
