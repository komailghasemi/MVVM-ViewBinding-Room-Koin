package com.trader.note.network

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class Net(private val client: OkHttpClient, private val gson: Gson) {

    suspend fun <T> get(
        url: String,
        clazz: Class<T>
    ): T? {
        return withContext(Dispatchers.IO) {
            val builder = Request.Builder()
                .url(url)
            val request = builder.build()

            val response = client.newCall(request).execute()
            val data = response.body?.string()
            if (response.code == 1) null else try {
                gson.fromJson(data, clazz)
            } catch (ex: Exception) {
                null
            }
        }


    }
}