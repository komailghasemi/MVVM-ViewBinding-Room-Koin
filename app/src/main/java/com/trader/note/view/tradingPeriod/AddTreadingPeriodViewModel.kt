package com.trader.note.view.tradingPeriod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trader.note.model.dao.TradingPeriodDao
import com.trader.note.model.tables.TradingPeriod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTreadingPeriodViewModel(private val tradingPeriodDao: TradingPeriodDao) : ViewModel() {
    fun insert(tradingPeriod: TradingPeriod) = viewModelScope.launch(Dispatchers.IO) {
        tradingPeriodDao.insert(tradingPeriod)
    }
}