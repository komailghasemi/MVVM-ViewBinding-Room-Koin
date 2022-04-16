package com.trader.note.view.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.marcinorlowski.fonty.Fonty
import com.trader.note.R
import com.trader.note.databinding.ItemTradingPeriodBinding
import com.trader.note.utils.toPersianString
import com.trader.note.view.viewsModel.TradingPeriodModel


class TradingPeriodAdapter :
    PagingDataAdapter<TradingPeriodModel, TradingPeriodAdapter.ViewHolder>(diffCallback) {

    private var listener: ((TradingPeriodModel) -> Unit)? = null

    fun setOnClickListener(listener: (TradingPeriodModel) -> Unit) {
        this.listener = listener
    }

    class ViewHolder(val binding: ItemTradingPeriodBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<TradingPeriodModel>() {
            override fun areItemsTheSame(
                oldItem: TradingPeriodModel,
                newItem: TradingPeriodModel
            ): Boolean {
                return oldItem.uid == newItem.uid // id
            }

            override fun areContentsTheSame(
                oldItem: TradingPeriodModel,
                newItem: TradingPeriodModel
            ): Boolean {
                return oldItem.periodName == newItem.periodName
                        && oldItem.startDate == newItem.startDate
                        && oldItem.endDate == newItem.endDate
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.txtName.text = "#${item?.periodName?.replace(" ", "_")}"

        holder.binding.lytIsEnablePeriod.backgroundTintList = ColorStateList.valueOf(
            holder.binding.root.context.getColor(
                if (item?.endDate == null) R.color.green else R.color.red
            )
        )
        holder.binding.txtDate.text = item?.startDate?.toPersianString() + if (item?.endDate == null) "" else " - ${item.endDate.toPersianString()}"

        holder.binding.root.setOnClickListener {
            listener?.invoke(item!!)
        }

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