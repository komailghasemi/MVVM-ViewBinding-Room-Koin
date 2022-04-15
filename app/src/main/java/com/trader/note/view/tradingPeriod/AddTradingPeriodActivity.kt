package com.trader.note.view.tradingPeriod

import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.snackbar.Snackbar
import com.trader.note.databinding.ActivityAddTradingPeriodBinding
import com.trader.note.utils.*
import com.trader.note.view.UI
import com.trader.note.view.trade.TradesActivity
import org.koin.android.ext.android.inject

class AddTradingPeriodActivity : UI<ActivityAddTradingPeriodBinding>() {

    companion object {
        const val TRADE_PERIOD_ID = "TRADE_PERIOD_ID"
    }
    private val vm: AddTradingPeriodViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.setBindingInflater(ActivityAddTradingPeriodBinding.inflate(LayoutInflater.from(this)))
        super.onCreate(savedInstanceState)

        uiSettings()
        vm.viewCreated(intent.getIntExtra(TradesActivity.TRADE_PERIOD_ID, -1))
        observers()
    }

    private fun uiSettings() {
        binding.edtInitialInvestment.editText?.currency()
    }

    private fun event() {
        binding.edtName.editText?.addTextChangedEvent(vm::setName)
        binding.edtInitialInvestment.editText?.addTextChangedEvent(vm::setInitialInvestment)
        binding.sldMdd.addSlideEvent(vm::setMdd)
        binding.sldMcl.addSlideEvent(vm::setMcl)
        binding.btnBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener { vm.onSaveClicked() }
    }

    private fun observers() {

        vm.name.observe(this) {
            if (binding.edtName.editText?.text?.toString() != it)
                binding.edtName.editText?.setText(it)
        }

        vm.initialInvestment.observe(this) {
            if (binding.edtInitialInvestment.editText?.text()?.toDoubleOrNull() != it)
                binding.edtInitialInvestment.editText?.setText(it.toString())
        }

        vm.mdd.observe(this) { value ->
            if (binding.sldMdd.value.toInt() != value) {
                binding.sldMdd.value = value.toFloat()
            }
            binding.txtMdd.text = "${value.toInt()}%"
        }

        vm.mcl.observe(this) { value ->
            if (binding.sldMcl.value.toInt() != value) {
                binding.sldMcl.value = value.toFloat()

                if (binding.sldMdd.value < value)
                    binding.sldMdd.value = value.toFloat()
                binding.sldMdd.valueFrom = value.toFloat()
            }
            binding.txtMcl.text = value.toString()
        }

        vm.nameError.observe(this) {
            binding.edtName.error = it
        }

        vm.initialInvestmentError.observe(this) {
            binding.edtInitialInvestment.error = it
        }

        vm.rpt.observe(this) { rpt ->
            binding.txtRpt.text = "درصد ریسک در هر معامله (R.P.T) برابر است با $rpt درصد"
        }

        vm.nMax.observe(this) { nMax ->
            binding.txtMaxN.text = "تعداد مجاز معاملات باز به صورت همزمان برابر است با $nMax"
        }

        vm.onSaved.observe(this) {
            binding.root.snackbar("با موفقیت ذخیره شد", Snackbar.LENGTH_LONG)
                .setAction("بازگشت") {
                    finish()
                }.show()
        }
        vm.modelLoaded.observe(this) {
            event()
        }
    }
}