package com.trader.note.di

import android.app.Application
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.trader.note.api.Api
import com.trader.note.model.db.AppDatabase
import com.trader.note.network.Net
import com.trader.note.view.adapters.SymbolAdapter
import com.trader.note.view.adapters.TradingPeriodAdapter
import com.trader.note.view.main.MainViewModel
import com.trader.note.view.trade.AddTradeViewModel
import com.trader.note.view.tradingPeriod.AddTreadingPeriodViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val network = module {
    this.factory {
        GsonBuilder().setLenient().create()
    }
    this.single {
        OkHttpClient().newBuilder()
            .build()
    }
    this.single {
        Net(get(), get())
    }
    this.factory {
        Api(get())
    }
}

val viewModel = module {
    this.viewModel {
        MainViewModel(get())
    }
    this.viewModel {
        AddTreadingPeriodViewModel(get())
    }
    this.viewModel {
        AddTradeViewModel(get())
    }
}


val adapter = module {
    this.factory {
        TradingPeriodAdapter()
    }
    this.factory {
        SymbolAdapter(get())
    }
}


val db = module {

    fun providesDatabase(ctx: Application) = Room.databaseBuilder(
        ctx,
        AppDatabase::class.java, "trader_table"
    ).build()

    this.single { providesDatabase(androidApplication()) }
    this.single { get<AppDatabase>().tradingPeriodDao() }
    this.single { get<AppDatabase>().tradeDao() }
}
