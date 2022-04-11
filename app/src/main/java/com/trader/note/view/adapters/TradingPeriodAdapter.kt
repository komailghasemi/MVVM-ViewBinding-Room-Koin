package com.trader.note.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.marcinorlowski.fonty.Fonty
import com.trader.note.databinding.ItemTradingPeriodBinding

class TradingPeriodAdapter : PagingDataAdapter<String, TradingPeriodAdapter.ViewHolder>(diffCallback) {

    class ViewHolder(val binding: ItemTradingPeriodBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem // id
            }
            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.txtName.text = "#${item?.replace(" " , "_")}"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemTradingPeriodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        Fonty.setFonts(itemBinding.root as ViewGroup)
        return ViewHolder(itemBinding)
    }
}