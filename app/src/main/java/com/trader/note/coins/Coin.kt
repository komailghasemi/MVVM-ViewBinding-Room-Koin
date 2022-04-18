package com.trader.note.coins

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Coin(private val context: Context, private val gson: Gson) {
    fun getCoinList(): Flow<List<String>> = flow {
        val text = context.assets.open("coins.json").bufferedReader().readText()
        val data = gson.fromJson(text, Array<String>::class.java)
        emit(data.toList())
    }
}