package com.trader.note.view.trade


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.trader.note.databinding.ActivityTradesBinding
import com.trader.note.utils.log
import com.trader.note.utils.toCurrency
import com.trader.note.view.UI
import com.trader.note.view.adapters.tradeTable.TradeTableAdapter
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
        vm.viewCreated(intent.getIntExtra(TRADE_PERIOD_ID, -1))
        event()
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
        vm.trades.observe(this) { ld ->
            ld.observe(this) {
                tableAdapter.setAllItems(
                    it.colHeaders.toList(),
                    it.rowHeaders.toList(),
                    it.cells.toList()
                )
            }
        }
    }
}