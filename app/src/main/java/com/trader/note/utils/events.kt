package com.trader.note.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.android.material.slider.Slider
import com.trader.note.R

fun EditText.addTextChangedEvent(
    onChanged: (String?) -> Unit,
    deleteCommaOfCurrency: Boolean = true
) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            if (deleteCommaOfCurrency)
                onChanged(s?.toString()?.replace(",", ""))
            else
                onChanged(s?.toString())
        }
    })
}

fun Slider.addSlideEvent(onSlide: (Float) -> Unit) {
    addOnChangeListener { _, value, _ ->
            onSlide(value)
    }
}