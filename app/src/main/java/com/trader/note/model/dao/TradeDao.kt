package com.trader.note.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.trader.note.model.tables.Trade
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {

    @Query("SELECT * FROM trade WHERE trade_period_id = :id")
    fun getTradesByPeriodId(id: Int): Flow<List<Trade>>

    @Insert
    fun insert(t: Trade)
}