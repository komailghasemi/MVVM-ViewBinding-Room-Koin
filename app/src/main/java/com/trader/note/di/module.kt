package com.trader.note.di

import com.trader.note.view.adapters.TradingPeriodAdapter
import com.trader.note.view.main.MainViewModel
import com.trader.note.view.tradeInitInformation.TradeInitInfoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModel = module {
    this.viewModel {
        MainViewModel()
    }

    this.viewModel {
        TradeInitInfoViewModel()
    }
}


val adapter = module {
    this.factory {
        TradingPeriodAdapter()
    }
}