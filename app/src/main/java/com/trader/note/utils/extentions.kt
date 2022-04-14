package com.trader.note.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import com.marcinorlowski.fonty.Fonty
import kotlinx.coroutines.flow.Flow
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*


fun View.snackbar(text: String, duration: Int): Snackbar {
    val sb = Snackbar.make(this, text, duration)
    val tv = sb.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
    Fonty.setFonts(tv.parent as ViewGroup)

    return sb
}

fun Date.toPersianString(): String {
    val pDate = PersianDate(this)
    return PersianDateFormat.format(pDate, "Y/m/j")
}

fun log(l: Any?) {
    Log.e("Trader-Note", l.toString())
}


fun String.toCurrency(decimal: Int? = null): String? {
    return try {
        var number = this
        if (decimal != null) {
            number = "%.${decimal}f".format(this.toDouble())
        }
        val formatter = DecimalFormat("###,###,##0.##############################")
        formatter.format(number.toDouble())
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}

fun EditText.currency() {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            removeTextChangedListener(this)
            val text = s.toString().replace(",", "")

            if (text.contains(".")) {
                val number = text.split(".")
                setText(number[0].toCurrency() + "." + number[1])
            } else {
                setText(text.toCurrency())
            }

            setSelection(length())
            addTextChangedListener(this)
        }
    })
}

fun EditText.text() = text?.toString()?.replace(",", "")

fun Double.round(): BigDecimal {
    log(this)
    val num = this.toBigDecimal().toString().split(".")
    val number = num[1]
    for (i in number.indices) {
        if (number[i] != '0') {
            if (i < number.length - 1) {
                return (num[0] + "." + number.take(i + 2)).toBigDecimal()
            }
        }
    }
    return this.toBigDecimal()
}
