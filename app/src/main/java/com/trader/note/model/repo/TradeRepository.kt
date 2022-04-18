package com.trader.note.model.repo

import com.trader.note.model.dao.TradeDao
import com.trader.note.model.tables.Trade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TradeRepository(private val tradeDao: TradeDao) {

    fun getById(id: Int) = tradeDao.getById(id)
    fun getListByPeriodId(id: Int) = tradeDao.getTradesByPeriodId(id)
    suspend fun upsert(trade: Trade) = withContext(Dispatchers.IO) { tradeDao.upsert(trade) }

}