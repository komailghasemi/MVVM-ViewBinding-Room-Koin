package com.trader.note.api

import com.google.gson.internal.LinkedTreeMap
import com.trader.note.network.Net
import com.trader.note.network.models.Symbol
import com.trader.note.urls.searchSymbolUrl
import com.trader.note.urls.symbolPriceUrl

class Api(private val net : Net) {

    suspend fun getSymbols() : Symbol? = net.get(searchSymbolUrl.replace(":symbol", ""), Symbol::class.java)
    suspend fun getPriceOf(id : String) : Double?{
        val data = net.get(
            symbolPriceUrl.replace(":id", id),
            Any::class.java
        ) as? LinkedTreeMap<String, LinkedTreeMap<String, Double>>

        return data?.get(id)?.get("usd")
    }
}