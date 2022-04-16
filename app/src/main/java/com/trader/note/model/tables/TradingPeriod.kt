package com.trader.note.model.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "trading_period")
data class TradingPeriod(
    @ColumnInfo(name = "period_name") val periodName: String,
    @ColumnInfo(name = "initial_investment") val initialInvestment: Double,
    @ColumnInfo(name = "mdd") val MDD: Int,
    @ColumnInfo(name = "mcl") val MCL: Int,
    @ColumnInfo(name = "start_date") val startDate: Date,
    @ColumnInfo(name = "end_date") val endDate: Date? = null,
    @ColumnInfo(name = "description") val description: String? = null,
    @PrimaryKey(autoGenerate = true)
    val uid: Int? = null
)