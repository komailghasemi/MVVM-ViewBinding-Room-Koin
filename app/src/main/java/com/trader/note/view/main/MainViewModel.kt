package com.trader.note.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.trader.note.model.dao.TradingPeriodDao
import kotlinx.coroutines.flow.map


class MainViewModel(private val tradingPeriodDao: TradingPeriodDao) : ViewModel() {
    fun getTradingPeriodList() = Pager(
        config = PagingConfig(
            pageSize = 25,
            enablePlaceholders = true,
            maxSize = 100
        )
    ) {
        tradingPeriodDao.getAll()
    }.flow.map {
        it.map { tp ->
            tp.periodName
        }
    }.cachedIn(viewModelScope).asLiveData()


}
