package com.trader.note


import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.marcinorlowski.fonty.Fonty
import com.trader.note.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Fonty
            .context(this)
            .fontDir("fonts")
            .normalTypeface("regular.ttf")
            .boldTypeface("bold.ttf")
            .build()

        startKoin {
            androidContext(this@App)
            modules(network, db, viewModel, repo, adapter, other)
        }


        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )

    }
}