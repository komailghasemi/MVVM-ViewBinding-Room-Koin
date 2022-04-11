package com.trader.note.view

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.marcinorlowski.fonty.Fonty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.*
import kotlin.coroutines.CoroutineContext

open class UI<T : ViewBinding> : AppCompatActivity() {
    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeLang()
        val view = binding.root

        Fonty.setFonts(view as ViewGroup)
        setContentView(view)
    }

    private fun changeLang() {
        val config = resources.configuration
        val lang = "en" // your language code
        val locale = Locale(lang)
        Locale.setDefault(locale)
        config.setLocale(locale)

        createConfigurationContext(config)

    }

    fun setBindingInflater(binding: T) {
        this.binding = binding
    }

}