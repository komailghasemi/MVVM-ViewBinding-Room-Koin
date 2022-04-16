package com.trader.note.api

import com.google.gson.internal.LinkedTreeMap
import com.trader.note.network.Net
import com.trader.note.urls.symbolPriceUrl

class Api(private val net : Net) {

    suspend fun getPriceOf(vararg ids: String): LinkedTreeMap<String, LinkedTreeMap<String, Double>>? {
        return net.get(
            symbolPriceUrl.replace(":id", ids.joinToString(",")),
            Any::class.java
        ) as? LinkedTreeMap<String, LinkedTreeMap<String, Double>>
    }
}