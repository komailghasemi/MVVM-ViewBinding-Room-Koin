package com.trader.note.view.trade

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import com.app.libarary.hide
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

    private var periodId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.setBindingInflater(ActivityAddTradeBinding.inflate(LayoutInflater.from(this)))
        super.onCreate(savedInstanceState)

        periodId = intent.getIntExtra(TRADE_PERIOD_ID, -1)
        initUi()
        event()
        fetch()
    }

    private fun initUi() {
        binding.edtEnterPrice.editText?.currency()
        binding.edtSl.editText?.currency()
        binding.edtTargetPrice.editText?.currency()
        binding.edtBuyCommission.editText?.currency()
        binding.edtTargetDate.editText?.setText(Date().toPersianString())
        binding.edtTargetDate.editText?.tag = System.currentTimeMillis()
    }

    private fun event() {
        vm.searchSymbols.observe(this) {
            binding.prgAutoComplete.hide()
            symbolAdapter.addAll(it)
            binding.txtSymbols.setAdapter(symbolAdapter)
        }

        vm.symbolPrice.observe(this) {
            binding.txtPrice.text = "${it.toCurrency()} $"
        }

        binding.txtSymbols.setOnItemClickListener { _, _, position, _ ->
            val id = symbolAdapter.getItem(position)
            if (!id.isNullOrEmpty())
                vm.fetchPrice(id)
        }

        datePickerEvent()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            if (!validation())
                return@setOnClickListener

            val symbol = binding.edtSymbol.editText?.text.toString()
            val enterDate = Date()
            val enterPrice = binding.edtEnterPrice.editText?.text()?.toDouble()!!
            val sl = binding.edtSl.editText?.text()?.toDouble()!!
            val priceTarget = binding.edtTargetPrice.editText?.text()?.toDouble()!!
            val commission = binding.edtBuyCommission.editText?.text()?.toDouble() ?: 0.0
            val dateTarget = Date(binding.edtTargetDate.editText?.tag.toString().toLong())
            val description = binding.edtDescription.editText?.text.toString()

            vm.insert(
                Trade(
                    periodId,
                    symbol,
                    enterDate,
                    enterPrice,
                    sl,
                    priceTarget,
                    commission,
                    dateTarget,
                    description = description
                )
            ).invokeOnCompletion {
                if (it == null) {
                    binding.root.snackbar("با موفقیت ذخیره شد", Snackbar.LENGTH_LONG)
                        .setAction("بازگشت") {
                            finish()
                        }.show()
                }
            }
        }
    }

    private fun validation(): Boolean {
        if (binding.edtSymbol.editText?.text.isNullOrEmpty()) {
            binding.edtSymbol.error = "انتخاب نماد معاملاتی الزامیست"
            return false
        }

        if (binding.edtEnterPrice.editText?.text.isNullOrEmpty()) {
            binding.edtEnterPrice.error = "وارد کردن قیمت ورود الزامیست"
            return false
        }

        if (binding.edtSl.editText?.text.isNullOrEmpty()) {
            binding.edtSl.error = "وارد کردن حد ضرر الزامیست"
            return false
        }

        if (binding.edtTargetPrice.editText?.text.isNullOrEmpty()) {
            binding.edtTargetPrice.editText?.text = binding.edtEnterPrice.editText?.text
        }

        return true
    }

    private fun datePickerEvent() {
        binding.edtTargetDate.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("")
                .setTypeFace(Typeface.createFromAsset(assets, "fonts/regular.ttf"))
                .setSelection(System.currentTimeMillis()).build()

            datePicker.addOnPositiveButtonClickListener(object :
                MaterialPickerOnPositiveButtonClickListener<Long?> {
                override fun onPositiveButtonClick(selection: Long?) {
                    if (selection != null) {
                        binding.edtTargetDate.editText?.setText(Date(selection).toPersianString())
                        binding.edtTargetDate.editText?.tag = selection
                    }
                }
            })
            datePicker.show(supportFragmentManager, "aTag")
        }
    }

    private fun fetch() {
        vm.fetchSymbols()
    }


}