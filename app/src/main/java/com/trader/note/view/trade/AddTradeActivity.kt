package com.trader.note.view.trade

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import com.app.libarary.hide
import com.app.libarary.show
import com.google.android.material.snackbar.Snackbar
import com.trader.note.databinding.ActivityAddTradeBinding
import com.trader.note.utils.*
import com.trader.note.view.UI
import com.trader.note.view.adapters.SymbolAdapter
import com.xdev.arch.persiancalendar.datepicker.MaterialDatePicker
import com.xdev.arch.persiancalendar.datepicker.MaterialPickerOnPositiveButtonClickListener
import org.koin.android.ext.android.inject
import java.util.*


class AddTradeActivity : UI<ActivityAddTradeBinding>() {
    companion object {
        const val TRADE_PERIOD_ID = "TRADE_PERIOD_ID"
        const val TRADE_ID = "TRADE_ID"
    }

    private val symbolAdapter: SymbolAdapter by inject()
    private val vm: AddTradeViewModel by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.setBindingInflater(ActivityAddTradeBinding.inflate(LayoutInflater.from(this)))
        super.onCreate(savedInstanceState)

        uiSettings()
        observers()
        vm.viewCreated(intent.getIntExtra(TRADE_PERIOD_ID, -1) , intent.getIntExtra(TRADE_ID, -1))
        event()
    }

    private fun uiSettings() {
        binding.edtEnterPrice.editText?.currency()
        binding.edtSl.editText?.currency()
        binding.edtTargetPrice.editText?.currency()
        binding.edtBuyCommission.editText?.currency()
        binding.edtSellCommission.editText?.currency()
        binding.edtVolume.editText?.currency()
        binding.edtSellPrice.editText?.currency()
    }

    private fun event() {
        binding.edtSymbol.editText?.addTextChangedEvent(vm::setSymbol)
        binding.edtEnterPrice.editText?.addTextChangedEvent(vm::setEnterPrice)
        binding.edtSl.editText?.addTextChangedEvent(vm::setSl)
        binding.edtTargetPrice.editText?.addTextChangedEvent(vm::setTargetPrice)
        binding.edtBuyCommission.editText?.addTextChangedEvent(vm::setBuyCommission)
        binding.edtSellCommission.editText?.addTextChangedEvent(vm::setSellCommission)
        binding.edtVolume.editText?.addTextChangedEvent(vm::setVolume)
        binding.edtDescription.editText?.addTextChangedEvent(vm::setDescription)
        binding.edtSellPrice.editText?.addTextChangedEvent(vm::setSellPrice)
        targetDatePickerEvent()
        sellDatePickerEvent()
        binding.btnBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener { vm.onSaveClicked() }
    }

    private fun observers() {

        vm.symbols.observe(this) {
            symbolAdapter.addAll(it)
            binding.inputSymbol.setAdapter(symbolAdapter)
        }
        vm.symbolError.observe(this) {
            binding.edtSymbol.error = it
        }

        vm.symbol.observe(this) {
            if (binding.edtSymbol.editText?.text.toString() != it)
                binding.edtSymbol.editText?.setText(it)
        }

        vm.enterPrice.observe(this) {
            if (binding.edtEnterPrice.editText?.text()?.toDoubleOrNull() != it)
                binding.edtEnterPrice.editText?.setText(it.toString())
        }

        vm.sellPrice.observe(this) {
            if (binding.edtSellPrice.editText?.text()?.toDoubleOrNull() != it)
                binding.edtSellPrice.editText?.setText(it.toString())
        }
        vm.enterPriceError.observe(this) {
            binding.edtEnterPrice.error = it
        }
        vm.sl.observe(this) {
            if (binding.edtSl.editText?.text()?.toDoubleOrNull() != it)
                binding.edtSl.editText?.setText(it.toString())
        }
        vm.slError.observe(this) {
            binding.edtSl.error = it
        }
        vm.targetPrice.observe(this) {
            if (binding.edtTargetPrice.editText?.text()?.toDoubleOrNull() != it)
                binding.edtTargetPrice.editText?.setText(it.toString())
        }
        vm.targetDate.observe(this) {
            val date = Date(it).toPersianString()
            if (binding.edtTargetDate.editText?.text.toString() != date)
                binding.edtTargetDate.editText?.setText(date)
        }
        vm.sellDate.observe(this) {
            val date = Date(it).toPersianString()
            if (binding.edtSellDate.editText?.text.toString() != date)
                binding.edtSellDate.editText?.setText(date)
        }
        vm.buyCommission.observe(this) {
            if (binding.edtBuyCommission.editText?.text()?.toDoubleOrNull() != it)
                binding.edtBuyCommission.editText?.setText(it.toString())
        }
        vm.sellCommission.observe(this) {
            if (binding.edtSellCommission.editText?.text()?.toDoubleOrNull() != it)
                binding.edtSellCommission.editText?.setText(it.toString())
        }
        vm.volume.observe(this) {
            if (binding.edtVolume.editText?.text()?.toDoubleOrNull() != it)
                binding.edtVolume.editText?.setText(it.toString())
        }
        vm.volumeError.observe(this) {
            binding.edtVolume.error = it
        }
        vm.volumeHelper.observe(this) {
            binding.txtCalculatedVolume.show()
            binding.txtCalculatedVolume.text =
                "حجم مجاز با توجه مدیریت ریسک برابر است با ${it.toString().toCurrency()}"

        }
        vm.rrr.observe(this) {
            binding.txtCalculatedRRR.show()
            binding.txtCalculatedRRR.text = "ریسک به ریوارد این معامله برابر است با $it"

        }
        vm.sarBeSar.observe(this) {
            if (it == 0.0) {
                binding.txtCalculatedSarBeSar.hide()
            } else {
                binding.txtCalculatedSarBeSar.show()
                binding.txtCalculatedSarBeSar.text =
                    "قیمت سر به سر برابر است با ${it.toString().toCurrency()}"
            }
        }

        vm.description.observe(this) {
            if (binding.edtDescription.editText?.text.toString() != it)
                binding.edtDescription.editText?.setText(it)
        }

        vm.closed.observe(this){
            binding.btnSave.hide()
        }

        vm.onSaved.observe(this) {
            binding.root.snackbar("با موفقیت ذخیره شد", Snackbar.LENGTH_LONG)
                .setAction("بازگشت") {
                    finish()
                }.show()
        }
    }

    private fun targetDatePickerEvent() {
        binding.edtTargetDate.editText?.setOnClickListener {
            val datePicker = getDatePicker("تاریخ احتمالی برخورد با حد سود را انتخاب کنید")

            datePicker.addOnPositiveButtonClickListener(object :
                MaterialPickerOnPositiveButtonClickListener<Long?> {
                override fun onPositiveButtonClick(selection: Long?) {
                    if (selection != null) {
                        vm.setTargetDate(selection)
                    }
                }
            })
            datePicker.show(supportFragmentManager, "aTag")
        }
    }

    private fun sellDatePickerEvent(){
        binding.edtSellDate.editText?.setOnClickListener {
            val datePicker = getDatePicker("تاریخ فروش را انتخاب کنید")

            datePicker.addOnPositiveButtonClickListener(object :
                MaterialPickerOnPositiveButtonClickListener<Long?> {
                override fun onPositiveButtonClick(selection: Long?) {
                    if (selection != null) {
                        vm.setSellDate(selection)
                    }
                }
            })
            datePicker.show(supportFragmentManager, "aTag")
        }
    }

    private fun getDatePicker(title : String) = MaterialDatePicker.Builder
        .datePicker()
        .setTitleText(title)
        .setTypeFace(Typeface.createFromAsset(assets, "fonts/regular.ttf"))
        .setSelection(System.currentTimeMillis()).build()
}