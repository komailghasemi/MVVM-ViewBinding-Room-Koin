package com.trader.note.view.adapters.tradeTable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.marcinorlowski.fonty.Fonty
import com.trader.note.R
import com.trader.note.databinding.TableViewCellLayoutBinding
import com.trader.note.databinding.TableViewColumnHeaderLayoutBinding
import com.trader.note.databinding.TableViewCornerLayoutBinding
import com.trader.note.databinding.TableViewRowHeaderLayoutBinding


class TradeTableAdapter : AbstractTableAdapter<ColumnHeader?, RowHeader?, Cell?>() {


    private var onClickListener : ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener :(Int) -> Unit){
        this.onClickListener = listener
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val itemBinding = TableViewCellLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        Fonty.setFonts(itemBinding.root as ViewGroup)
        return CellViewHolder(itemBinding)
    }

    class CellViewHolder(val binding: TableViewCellLayoutBinding) : AbstractViewHolder(binding.root)

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: Cell?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        val viewHolder = holder as CellViewHolder
        viewHolder.binding.cellData.text = cellItemModel?.data

        viewHolder.binding.root.setOnClickListener { onClickListener?.invoke(rowPosition) }

        val rh = getRowHeaderItemViewType(rowPosition)
        if (rh == 1)
            viewHolder.binding.cellData.setBackgroundColor(
                viewHolder.binding.root.resources.getColor(
                    R.color.green,
                    null
                )
            )
        else
            viewHolder.binding.cellData.setBackgroundColor(
                viewHolder.binding.root.resources.getColor(
                    R.color.red,
                    null
                )
            )

        viewHolder.binding.cellContainer.layoutParams.width =
            LinearLayout.LayoutParams.WRAP_CONTENT
        viewHolder.binding.cellData.requestLayout()
    }

    override fun getRowHeaderItemViewType(position: Int): Int {
        val rh = getRowHeaderItem(position)
        return if (rh?.inProfit == true) {
            1
        } else {
            -1
        }
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        val itemBinding = TableViewColumnHeaderLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        Fonty.setFonts(itemBinding.root as ViewGroup)
        return ColumnHeaderViewHolder(itemBinding)
    }

    class ColumnHeaderViewHolder(val binding: TableViewColumnHeaderLayoutBinding) :
        AbstractViewHolder(binding.root)

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: ColumnHeader?,
        columnPosition: Int
    ) {
        val viewHolder = holder as ColumnHeaderViewHolder
        viewHolder.binding.columnHeaderTextView.text = columnHeaderItemModel?.data
        viewHolder.binding.columnHeaderContainer.layoutParams.width =
            LinearLayout.LayoutParams.WRAP_CONTENT
        viewHolder.binding.columnHeaderTextView.requestLayout()
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val itemBinding = TableViewRowHeaderLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        Fonty.setFonts(itemBinding.root as ViewGroup)
        return RowHeaderViewHolder(itemBinding)
    }

    class RowHeaderViewHolder(val binding: TableViewRowHeaderLayoutBinding) :
        AbstractViewHolder(binding.root)

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: RowHeader?,
        rowPosition: Int
    ) {
        val viewHolder = holder as RowHeaderViewHolder
        viewHolder.binding.rowHeaderTextView.text = rowHeaderItemModel?.data
        viewHolder.binding.root.setOnClickListener { onClickListener?.invoke(rowPosition) }

        val rh = getRowHeaderItemViewType(rowPosition)
        if (rh == 1)
            viewHolder.binding.rowHeaderTextView.setBackgroundColor(
                viewHolder.binding.root.resources.getColor(
                    R.color.green,
                    null
                )
            )
        else
            viewHolder.binding.rowHeaderTextView.setBackgroundColor(
                viewHolder.binding.root.resources.getColor(
                    R.color.red,
                    null
                )
            )

    }

    override fun onCreateCornerView(parent: ViewGroup): View {
        val itemBinding = TableViewCornerLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        Fonty.setFonts(itemBinding.root as ViewGroup)

        return itemBinding.root
    }
}