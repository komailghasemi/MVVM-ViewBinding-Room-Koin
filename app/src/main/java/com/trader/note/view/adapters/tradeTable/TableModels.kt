package com.trader.note.view.adapters.tradeTable

open class Cell(
    val data: String
)

class ColumnHeader(data: String) : Cell(data)
class RowHeader(data: String, val inProfit: Boolean, val tradeId: Int) : Cell(data)