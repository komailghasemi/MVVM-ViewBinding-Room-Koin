package com.trader.note.view.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.trader.note.R

class SymbolAdapter(context: Context) : ArrayAdapter<String>(context, R.layout.item_symbol) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        (view as TextView).typeface = Typeface.createFromAsset(context.assets , "fonts/regular.ttf")
        return view
    }
}