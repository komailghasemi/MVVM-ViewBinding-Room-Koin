package com.trader.note.utils

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.marcinorlowski.fonty.Fonty

fun View.snackbar(text : String , duration : Int) : Snackbar{
    val sb = Snackbar.make(this ,text , duration)
    val tv = sb.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
    Fonty.setFonts(tv.parent as ViewGroup)

    return sb
}