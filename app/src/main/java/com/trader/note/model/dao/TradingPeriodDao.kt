package com.trader.note.model.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.trader.note.model.tables.TradingPeriod

@Dao
interface TradingPeriodDao {
    @Query("SELECT * FROM trading_period order by end_date")
    fun getAll(): PagingSource<Int, TradingPeriod>

    @Query("SELECT * FROM trading_period where uid = :id")
    fun getById(id: Int): TradingPeriod

    @Insert
    fun insert(tp: TradingPeriod)
}