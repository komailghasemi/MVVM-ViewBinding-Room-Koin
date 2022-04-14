package com.trader.note.view.tradingPeriod

import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.snackbar.Snackbar
import com.trader.note.databinding.ActivityAddTradingPeriodBinding
import com.trader.note.utils.addSlideEvent
import com.trader.note.utils.addTextChangedEvent
import com.trader.note.utils.currency
import com.trader.note.utils.snackbar
import com.trader.note.view.UI
import org.koin.android.ext.android.inject

class AddTradingPeriodActivity : UI<ActivityAddTradingPeriodBinding>() {

    private val vm: AddTreadingPeriodViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.setBindingInflater(ActivityAddTradingPeriodBinding.inflate(LayoutInflater.from(this)))
        super.onCreate(savedInstanceState)
        initUi()
        event()
        observers()
    }

    private fun initUi() {
        binding.edtInitialInvestment.editText?.currency()
    }

    private fun event() {
        binding.edtName.editText?.addTextChangedEvent(vm::nameEvent)
        binding.edtInitialInvestment.editText?.addTextChangedEvent(vm::initialInvestmentEvent)
        binding.sldMdd.addSlideEvent(vm::mddEvent)
        binding.sldMcl.addSlideEvent(vm::mclEvent)
        binding.btnBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener { vm.onSaveClicked() }
    }

    private fun observers() {
        vm.nameError.observe(this) {
            binding.edtName.error = it
        }

        vm.initialInvestmentError.observe(this) {
            binding.edtInitialInvestment.error = it
        }

        vm.mdd.observe(this) { value ->
            binding.txtMdd.text = "${value.toInt()}%"
        }

        vm.mcl.observe(this) { value ->
            binding.txtMcl.text = value.toInt().toString()
            if (binding.sldMdd.value < value)
                binding.sldMdd.value = value.toFloat()
            binding.sldMdd.valueFrom = value.toFloat()
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
    }
}