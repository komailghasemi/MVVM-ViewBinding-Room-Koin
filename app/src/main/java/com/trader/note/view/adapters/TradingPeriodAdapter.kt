package com.trader.note.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marcinorlowski.fonty.Fonty
import com.trader.note.databinding.ItemTradingPriodBinding

class TradingPeriodAdapter : RecyclerView.Adapter<TradingPeriodAdapter.ViewHolder>(){

    private val dataSet : MutableList<String> = mutableListOf()

    fun set(dataSet : List<String>){
        this.dataSet.clear()
        this.dataSet.addAll(dataSet)
        notifyItemRangeChanged(0 , dataSet.size)
    }

    class ViewHolder(val binding: ItemTradingPriodBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemTradingPriodBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        Fonty.setFonts(itemBinding.root as ViewGroup)
        return ViewHolder(itemBinding)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
      //  val news = news[position]

    }

    override fun getItemCount() = dataSet.size
}