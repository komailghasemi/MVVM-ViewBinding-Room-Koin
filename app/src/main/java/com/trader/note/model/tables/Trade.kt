package com.trader.note.model.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "trade")
data class Trade(
    @ColumnInfo(name = "trade_period_id") val tradePeriodId: Int,
    @ColumnInfo(name = "symbol") val symbol: String,
    @ColumnInfo(name = "enter_date") val enterDate: Date,
    @ColumnInfo(name = "enter_price") val enterPrice: Double,
    @ColumnInfo(name = "volume") val volume: Double,
    @ColumnInfo(name = "sl") val sl: Double,
    @ColumnInfo(name = "price_target") val priceTarget: Double,
    @ColumnInfo(name = "buy_commission") val buy_commission: Double,
    @ColumnInfo(name = "sell_commission") val sell_commission: Double,
    @ColumnInfo(name = "date_target") val dateTarget: Date? = null,
    @ColumnInfo(name = "sell_date") val sellDate: Date? = null, // close
    @ColumnInfo(name = "sell_price") val sellPrice: Double? = null, // close
    @ColumnInfo(name = "description") val description: String? = null,


    @PrimaryKey(autoGenerate = true)
    val uid: Int? = null
)