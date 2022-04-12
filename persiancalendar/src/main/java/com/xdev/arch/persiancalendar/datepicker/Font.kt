package com.xdev.arch.persiancalendar.datepicker

import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout

fun ViewGroup.setFontsRaw(typeface : Typeface) {
    for (i in 0 until this.childCount) {
        val view = this.getChildAt(i)

        if (EditText::class.java.isInstance(view)) {
            (view as EditText).typeface = typeface

        } else if (TextView::class.java.isInstance(view)) {
            (view as TextView).typeface = typeface

        } else if (Button::class.java.isInstance(view)) {
            (view as Button).typeface = typeface

        } else if (NavigationView::class.java.isInstance(view)) {
            val nv = view as NavigationView
            // change font in header (if any)
            val headerCount = nv.headerCount
            for (headerIndex in 0 until headerCount) {
                (nv.getHeaderView(headerIndex) as ViewGroup).setFontsRaw(typeface)
            }

        } else if (TextInputLayout::class.java.isInstance(view)) {
            val et = (view as TextInputLayout).editText
            if (et != null) {
                view.typeface = typeface
                et.typeface = typeface
            }

        } else if (ViewGroup::class.java.isInstance(view)) {
            (view as ViewGroup).setFontsRaw(typeface)
        }
    }
}