package com.trader.note.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.trader.note.model.tables.Trade
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {

    @Query("SELECT * FROM trade WHERE trade_period_id = :id order by sell_date")
    fun getTradesByPeriodId(id: Int): Flow<List<Trade>>

    @Query("SELECT * FROM trade WHERE uid = :id")
    fun getById(id: Int): Trade

    @Insert
    fun insert(t: Trade) : Long

    @Update
    fun update(t: Trade)
}