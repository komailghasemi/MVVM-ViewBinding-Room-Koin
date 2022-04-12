package com.trader.note.view.viewsModel

import java.util.*

data class TradingPeriodModel(
    val uid: Int,
    val periodName: String,
    val startDate: Date,
    val endDate: Date? = null
)