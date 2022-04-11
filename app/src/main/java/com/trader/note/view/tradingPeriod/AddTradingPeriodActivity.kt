package com.trader.note.view.tradingPeriod

import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.snackbar.Snackbar
import com.trader.note.databinding.ActivityAddTradingPeriodBinding
import com.trader.note.model.tables.TradingPeriod
import com.trader.note.utils.snackbar
import com.trader.note.view.UI
import org.koin.android.ext.android.inject

class AddTradingPeriodActivity : UI<ActivityAddTradingPeriodBinding>() {

    private val vm: AddTreadingPeriodViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.setBindingInflater(ActivityAddTradingPeriodBinding.inflate(LayoutInflater.from(this)))
        super.onCreate(savedInstanceState)

        event()
    }

    private fun event() {

        binding.sldMdd.addOnChangeListener { _, value, _ ->
            binding.txtMdd.text = "${value.toInt()}%"
            calculate()
        }

        binding.sldMcl.addOnChangeListener { _, value, _ ->
            binding.txtMcl.text = value.toInt().toString()
            if (binding.sldMdd.value < value)
                binding.sldMdd.value = value
            binding.sldMdd.valueFrom = value

            calculate()

        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            if (binding.edtName.editText?.text.isNullOrEmpty()) {
                binding.edtName.error = "وارد کردن نام الزامیست"
            }

            if (binding.edtInitialInvestment.editText?.text.isNullOrEmpty()) {
                binding.edtInitialInvestment.error = "وارد کردن سرمایه اولیه الزامیست"
            }

            val name = binding.edtName.editText?.text.toString()
            val ii = binding.edtInitialInvestment.editText?.text!!.toString().toDouble()
            val mdd = binding.sldMdd.value.toInt()
            val mcl = binding.sldMcl.value.toInt()
            val rpt = binding.sldMdd.value / binding.sldMcl.value
            val nMax = (binding.sldMdd.value / rpt).toInt()

            vm.insert(TradingPeriod(name, ii, mdd, mcl, rpt, nMax)).invokeOnCompletion {
                if (it == null) {
                    binding.root.snackbar("با موفقیت ذخیره شد", Snackbar.LENGTH_LONG)
                        .setAction("بازگشت") {
                            finish()
                        }.show()
                }
            }
        }
    }


    private fun calculate() {
        val rpt = binding.sldMdd.value / binding.sldMcl.value
        binding.txtRpt.text = "درصد ریسک (R.P.T) در هر معامله برابر است با $rpt درصد"
        val maxN = binding.sldMdd.value / rpt
        binding.txtMaxN.text = "تعداد مجاز معاملات باز به صورت همزمان برابر است با ${maxN.toInt()}"
    }
}