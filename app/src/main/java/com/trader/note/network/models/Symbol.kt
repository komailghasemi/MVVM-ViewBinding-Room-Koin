package com.trader.note.network.models

data class Symbol(
    val coins : List<SymbolInfo>
)

data class SymbolInfo(
    val id: String
)
