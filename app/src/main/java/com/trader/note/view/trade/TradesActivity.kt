package com.trader.note.view.trade


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.app.libarary.hide
import com.trader.note.databinding.ActivityTradesBinding
import com.trader.note.utils.toCurrency
import com.trader.note.view.UI
import com.trader.note.view.adapters.tradeTable.TradeTableAdapter
import com.trader.note.view.tradingPeriod.AddTradingPeriodActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TradesActivity : UI<ActivityTradesBinding>() {
    companion object {
        const val TRADE_PERIOD_ID = "TRADE_PERIOD_ID"
    }

    private val vm: TradesViewModel by viewModel()
    private val tableAdapter: TradeTableAdapter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.setBindingInflater(ActivityTradesBinding.inflate(LayoutInflater.from(this)))
        super.onCreate(savedInstanceState)

        uiSettings()
        observers()
        event()
    }

    override fun onResume() {
        super.onResume()
        vm.viewCreated(intent.getIntExtra(TRADE_PERIOD_ID, -1))
    }

    private fun uiSettings() {
        binding.table.setAdapter(tableAdapter)
    }

    private fun event() {
        binding.fabNew.setOnClickListener {
            startActivity(Intent(this, AddTradeActivity::class.java).apply {
                putExtra(AddTradeActivity.TRADE_PERIOD_ID, vm.getPeriodId())
            })
        }
        tableAdapter.setOnItemClickListener {
            val row = tableAdapter.getRowHeaderItem(it)

            startActivity(Intent(this, AddTradeActivity::class.java).apply {
                putExtra(AddTradeActivity.TRADE_PERIOD_ID, vm.getPeriodId())
                putExtra(AddTradeActivity.TRADE_ID, row?.tradeId)
            })
        }

        binding.lytBoard.setOnClickListener {
            startActivity(Intent(this, AddTradingPeriodActivity::class.java).apply {
                putExtra(AddTradingPeriodActivity.TRADE_PERIOD_ID, vm.getPeriodId())
            })
        }
    }

    private fun observers() {
        vm.periodName.observe(this) {
            binding.txtPeriodName.text = it
        }
        vm.initialInvestment.observe(this) {
            binding.txtInitialInvestment.text = it.toString().toCurrency()
        }
        vm.nMax.observe(this) {
            binding.txtNMax.text = it.toString()
        }
        vm.wr.observe(this) {
            binding.txtWr.text = "$it%"
        }
        vm.targetInvestment.observe(this) {
            binding.txtInvestment.text = "~${it.toString().toCurrency(2)}"

        }
        vm.closed.observe(this) {
            binding.fabNew.hide()
        }
        vm.trades.observe(this) { ld ->
            ld.observe(this) {
                binding.prgLoading.hide()
                tableAdapter.setAllItems(
                    it.colHeaders.toList(),
                    it.rowHeaders.toList(),
                    it.cells.toList()
                )
            }
        }
    }
}