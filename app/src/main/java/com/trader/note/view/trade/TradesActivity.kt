package com.trader.note.view.trade


import android.os.Bundle
import android.view.LayoutInflater
import com.trader.note.databinding.ActivityTradesBinding
import com.trader.note.view.UI
import com.trader.note.view.adapters.tradeTable.Cell
import com.trader.note.view.adapters.tradeTable.ColumnHeader
import com.trader.note.view.adapters.tradeTable.RowHeader
import com.trader.note.view.adapters.tradeTable.TradeTableAdapter

class TradesActivity : UI<ActivityTradesBinding>() {
    companion object{
        const val TRADE_PERIOD_ID = "TRADE_PERIOD_ID"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.setBindingInflater(ActivityTradesBinding.inflate(LayoutInflater.from(this)))
        super.onCreate(savedInstanceState)
        val ii = 10
        val bb = 10
        val mRowHeaderList: MutableList<RowHeader?> = mutableListOf()
        for (i in 0..bb){
            mRowHeaderList.add(RowHeader("#$i"))
        }

        val mColumnHeaderList: MutableList<ColumnHeader?> = mutableListOf()
        for (i in 0..ii){
            mColumnHeaderList.add(ColumnHeader("تیتر $i"))
        }
        val mCellList: MutableList<MutableList<Cell?>?> = mutableListOf()
        for (i in 0..bb){
            val list = mutableListOf<Cell?>()
            for(j in 0..ii){
                list.add(Cell("سلولسلولسلولسلولسلولسلولسلول $i $j"))
            }
            mCellList.add(list)
        }
        val adapter = TradeTableAdapter()
        binding.contentContainer.setAdapter(adapter)
        adapter.setAllItems(mColumnHeaderList, mRowHeaderList, mCellList)


    }
}