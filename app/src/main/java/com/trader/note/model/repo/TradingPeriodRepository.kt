package com.trader.note.model.repo

import com.trader.note.model.dao.TradingPeriodDao
import com.trader.note.model.tables.TradingPeriod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TradingPeriodRepository(private val tradingPeriodDao: TradingPeriodDao) {

    fun getAll() = tradingPeriodDao.getAll()
    fun getById(id: Int) = tradingPeriodDao.getById(id)
    suspend fun upsert(tp: TradingPeriod) =
        withContext(Dispatchers.IO) { tradingPeriodDao.upsert(tp) }

}