package com.trader.note.model.repo

import androidx.room.ColumnInfo
import java.util.*

data class TradingPeriodModel(
    @ColumnInfo(name = "period_name") val periodName: String,
    @ColumnInfo(name = "start_date") val startDate: Date,
    @ColumnInfo(name = "end_date") val endDate: Date? = null,
    val uid: Int
)