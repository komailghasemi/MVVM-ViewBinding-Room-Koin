package com.trader.note.model.dao

import androidx.room.*
import com.trader.note.model.tables.Trade
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {

    @Query("SELECT * FROM trade WHERE trade_period_id = :id order by sell_date")
    fun getTradesByPeriodId(id: Int): Flow<List<Trade>>

    @Query("SELECT * FROM trade WHERE uid = :id")
    fun getById(id: Int): Flow<Trade?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(t: Trade) : Long?
}