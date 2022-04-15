package com.trader.note.view.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import com.trader.note.databinding.ActivityMainBinding
import com.trader.note.view.UI
import com.trader.note.view.adapters.TradingPeriodAdapter
import com.trader.note.view.trade.TradesActivity
import com.trader.note.view.tradingPeriod.AddTradingPeriodActivity
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : UI<ActivityMainBinding>() {
    private val vm: MainViewModel by viewModel()
    private val tradingPeriodAdapter: TradingPeriodAdapter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.setBindingInflater(ActivityMainBinding.inflate(LayoutInflater.from(this)))
        super.onCreate(savedInstanceState)

        uiSettings()
        observers()
        event()
    }

    private fun uiSettings() {
        recycler()
    }

    private fun recycler() {
        binding.recycle.animation(false)
        binding.recycle.setHasFixedSize(true)
        binding.recycle.setLayoutManager()
        binding.recycle.setNestedScrolling(true)

        binding.recycle.setAdapter(tradingPeriodAdapter)
    }

    private fun observers() {
        vm.tradingPeriods.observe(this) {
            lifecycleScope.launch {
                tradingPeriodAdapter.submitData(it)
            }
        }
    }

    private fun event() {
        binding.fabNew.setOnClickListener {
            startActivity(Intent(this, AddTradingPeriodActivity::class.java))
        }
        tradingPeriodAdapter.setOnClickListener {
            startActivity(Intent(this, TradesActivity::class.java).apply {
                putExtra(TradesActivity.TRADE_PERIOD_ID, it.uid)
            })
        }
    }
}