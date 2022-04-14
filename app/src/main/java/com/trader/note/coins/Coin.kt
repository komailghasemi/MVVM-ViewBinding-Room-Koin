package com.trader.note.coins

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Coin(private val context: Context, private val gson: Gson) {
    suspend fun getCoinList(): List<String> {
        return withContext(Dispatchers.IO) {
            val text = context.assets.open("coins.json").bufferedReader().readText()
            val data = gson.fromJson(text, Array<String>::class.java)
            data.toList()
        }
    }
}