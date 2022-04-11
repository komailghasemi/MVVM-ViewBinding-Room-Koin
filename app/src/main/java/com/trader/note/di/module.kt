package com.trader.note.di

import android.app.Application
import androidx.room.Room
import com.trader.note.model.db.AppDatabase
import com.trader.note.view.adapters.TradingPeriodAdapter
import com.trader.note.view.main.MainViewModel
import com.trader.note.view.tradingPeriod.AddTreadingPeriodViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModel = module {
    this.viewModel {
        MainViewModel(get())
    }
    this.viewModel {
        AddTreadingPeriodViewModel(get())
    }
}


val adapter = module {
    this.factory {
        TradingPeriodAdapter()
    }
}


val db = module {

    fun providesDatabase(ctx: Application) = Room.databaseBuilder(
        ctx,
        AppDatabase::class.java, "trader_table"
    ).build()

    fun providesDao(db: AppDatabase) = db.tradingPeriodDao()

    this.single { providesDatabase(androidApplication()) }
    this.single { providesDao(get()) }
}
