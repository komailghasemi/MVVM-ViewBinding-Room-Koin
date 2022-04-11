package com.trader.note.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.trader.note.model.converter.DateTypeConverter
import com.trader.note.model.dao.TradingPeriodDao
import com.trader.note.model.tables.TradingPeriod

@Database(entities = [TradingPeriod::class], version = 1)
@TypeConverters(DateTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tradingPeriodDao(): TradingPeriodDao

}