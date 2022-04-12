package com.trader.note.view.trade

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import com.app.libarary.hide
import com.trader.note.databinding.ActivityAddTradeBinding
import com.trader.note.utils.currency
import com.trader.note.utils.toCurrency
import com.trader.note.utils.toPersianString
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

        event()
        fetch()
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

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.txtSymbols.setOnItemClickListener { _, _, position, _ ->
            val id = symbolAdapter.getItem(position)
            if (!id.isNullOrEmpty())
                vm.fetchPrice(id)
        }

        binding.edtEnterPrice.editText?.currency()
        binding.edtSl.editText?.currency()
        binding.edtTargetPrice.editText?.currency()
        binding.edtCommission.editText?.currency()

        binding.txtDateTarget.text = Date().toPersianString()

        (binding.txtDateTarget.parent as LinearLayoutCompat).setOnClickListener {


            val datePicker = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("")
                .setTypeFace(Typeface.createFromAsset(assets , "fonts/regular.ttf"))
                .setSelection(System.currentTimeMillis()).build()

            datePicker.addOnPositiveButtonClickListener(object :
                MaterialPickerOnPositiveButtonClickListener<Long?> {
                override fun onPositiveButtonClick(selection: Long?) {
                    if (selection != null)
                        binding.txtDateTarget.text = Date(selection).toPersianString()
                }
            })
            datePicker.show(supportFragmentManager, "aTag")
        }
    }

    private fun fetch() {
        vm.fetchSymbols()
    }


}