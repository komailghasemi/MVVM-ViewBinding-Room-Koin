package com.trader.note.view.trade

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import com.app.libarary.hide
import com.app.libarary.show
import com.google.android.material.snackbar.Snackbar
import com.trader.note.databinding.ActivityAddTradeBinding
import com.trader.note.model.tables.Trade
import com.trader.note.utils.*
import com.trader.note.view.UI
import com.trader.note.view.adapters.SymbolAdapter
import com.xdev.arch.persiancalendar.datepicker.*
import org.koin.android.ext.android.inject
import java.util.*


class AddTradeActivity : UI<ActivityAddTradeBinding>() {
    companion object {
        const val TRADE_PERIOD_ID = "TRADE_PERIOD_ID"
    }

    private val symbolAdapter: SymbolAdapter by inject()
    private val vm: AddTradeViewModel by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.setBindingInflater(ActivityAddTradeBinding.inflate(LayoutInflater.from(this)))
        super.onCreate(savedInstanceState)

        vm.setPeriodId(intent.getIntExtra(TRADE_PERIOD_ID, -1))
        initUi()
        event()
        observers()
        fetch()
        load()
    }

    private fun initUi() {
        binding.edtEnterPrice.editText?.currency()
        binding.edtSl.editText?.currency()
        binding.edtTargetPrice.editText?.currency()
        binding.edtBuyCommission.editText?.currency()
        binding.edtSellCommission.editText?.currency()
        binding.edtVolume.editText?.currency()
    }

    private fun event() {
        binding.edtSymbol.editText?.addTextChangedEvent(vm::symbolEvent)
        binding.edtEnterPrice.editText?.addTextChangedEvent(vm::enterPriceEvent)
        binding.edtSl.editText?.addTextChangedEvent(vm::slEvent)
        binding.edtTargetPrice.editText?.addTextChangedEvent(vm::targetPriceEvent)
        binding.edtBuyCommission.editText?.addTextChangedEvent(vm::buyCommissionEvent)
        binding.edtSellCommission.editText?.addTextChangedEvent(vm::sellCommissionEvent)
        binding.edtVolume.editText?.addTextChangedEvent(vm::volumeEvent)
        binding.edtDescription.editText?.addTextChangedEvent(vm::descriptionEvent)
        datePickerEvent()
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
        vm.volumeError.observe(this) {
            binding.edtVolume.error = it
        }
        vm.enterPriceError.observe(this) {
            binding.edtEnterPrice.error = it
        }
        vm.slError.observe(this) {
            binding.edtSl.error = it
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
        vm.targetDate.observe(this) {
            binding.edtTargetDate.editText?.setText(Date(it).toPersianString())
        }
        vm.onSaved.observe(this) {
            binding.root.snackbar("با موفقیت ذخیره شد", Snackbar.LENGTH_LONG)
                .setAction("بازگشت") {
                    finish()
                }.show()
        }
    }

    private fun datePickerEvent() {
        binding.edtTargetDate.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("تاریخ احتمالی برخورد با حد سود را انتخاب کنید")
                .setTypeFace(Typeface.createFromAsset(assets, "fonts/regular.ttf"))
                .setSelection(System.currentTimeMillis()).build()

            datePicker.addOnPositiveButtonClickListener(object :
                MaterialPickerOnPositiveButtonClickListener<Long?> {
                override fun onPositiveButtonClick(selection: Long?) {
                    if (selection != null) {
                        vm.targetDateEvent(selection)
                    }
                }
            })
            datePicker.show(supportFragmentManager, "aTag")
        }
    }

    private fun fetch() {
        vm.fetchSymbols()
    }

    private fun load(){
      //  binding.edtDescription.editText?.setText("تست")
    }
}