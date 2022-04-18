package com.trader.note.model.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.trader.note.model.repo.TradingPeriodModel
import com.trader.note.model.tables.TradingPeriod
import kotlinx.coroutines.flow.Flow

@Dao
interface TradingPeriodDao {
    @Query("SELECT uid , period_name , start_date , end_date FROM trading_period order by end_date")
    fun getAll(): PagingSource<Int, TradingPeriodModel>

    @Query("SELECT * FROM trading_period where uid = :id")
    fun getById(id: Int): Flow<TradingPeriod?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tp: TradingPeriod): Long?

}