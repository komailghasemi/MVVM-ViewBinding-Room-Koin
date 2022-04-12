package com.trader.note.model.dao

import androidx.room.Dao
import androidx.room.Insert
import com.trader.note.model.tables.Trade
@Dao
interface TradeDao {
    @Insert
    fun insert(t : Trade)
}