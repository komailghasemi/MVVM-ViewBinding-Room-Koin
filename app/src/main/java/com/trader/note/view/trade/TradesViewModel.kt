package com.trader.note.view.trade

import androidx.lifecycle.*
import com.trader.note.api.Api
import com.trader.note.model.dao.TradeDao
import com.trader.note.model.dao.TradingPeriodDao
import com.trader.note.model.tables.TradingPeriod
import com.trader.note.utils.toCurrency
import com.trader.note.utils.toPersianString
import com.trader.note.view.adapters.tradeTable.Cell
import com.trader.note.view.adapters.tradeTable.ColumnHeader
import com.trader.note.view.adapters.tradeTable.RowHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TradesTableViewModel {
    val rowHeaders = mutableListOf<RowHeader>()
    val colHeaders = mutableListOf<ColumnHeader>()
    val cells = mutableListOf<MutableList<Cell>>()
}

class TradesViewModel(
    private val tradeDao: TradeDao,
    private val tradingPeriodDao: TradingPeriodDao,
    private val api: Api
) : ViewModel() {

    private lateinit var period: TradingPeriod

    fun viewCreated(periodId: Int) {
        loadPeriod(if (periodId == -1) null else periodId)
    }

    private fun loadPeriod(periodId: Int?) = viewModelScope.launch(Dispatchers.IO) {
        if (periodId != null) {
            period = tradingPeriodDao.getById(periodId)
            launch(Dispatchers.Main) {
                setName(period.periodName)
                setInitialInvestment(period.initialInvestment)
                val rpt = period.MDD / period.MCL
                setNMax(period.MDD / rpt)
                _trades.value = getTrades(period.uid!!)
                if(period.endDate != null){
                    _closed.value = Unit
                }
            }
        }
    }

    private fun setNMax(nMax: Int?) {
        _nMax.value = nMax
    }

    private fun setName(name: String?) {
        _periodName.value = name
    }

    private fun setInitialInvestment(ii: Double?) {
        _initialInvestment.value = ii
    }

    fun getPeriodId() = period.uid

    // Models

    private fun getHeaders() =
        arrayOf(
            ColumnHeader("نام نماد"),
            ColumnHeader("وضعیت"),
            ColumnHeader("قیمت"),
            ColumnHeader("ورود"),
            ColumnHeader("سود/زیان"),
            ColumnHeader("تاریخ"),
            ColumnHeader("حجم"),
            ColumnHeader("حد ضرر"),
            ColumnHeader("تارگت قیمتی"),
            ColumnHeader("تارگت زمانی"),
            ColumnHeader("ریسک به ریوارد"),
            ColumnHeader("خروج"),
            ColumnHeader("تاریخ خروج")
        )

    private fun getTrades(id: Int) = tradeDao.getTradesByPeriodId(id).map {
        withContext(Dispatchers.IO) {
            val ttvm = TradesTableViewModel()
            var loss = 0
            var targetCalculateInvestment = 0.0
            ttvm.colHeaders.addAll(
                getHeaders()
            )

            val prices = api.getPriceOf(*it.map { t -> t.symbol }.toTypedArray())

            for (rowIndex in it.indices) {
                val currentPrice = prices?.get(it[rowIndex].symbol)?.get("usd")
                val enterPrice = it[rowIndex].enterPrice
                var percent = 0.0
                val closed = it[rowIndex].sellPrice != null
                if (currentPrice != null && !closed) {
                    percent = ((currentPrice - enterPrice) / enterPrice) * 100
                } else if (closed) {
                    percent = ((it[rowIndex].sellPrice!! - enterPrice) / enterPrice) * 100

                }

                ttvm.rowHeaders.add(RowHeader("#${rowIndex + 1}", percent >= 0, it[rowIndex].uid!!))

                if (percent < 0)
                    loss++

                val state = if (!closed) {
                    targetCalculateInvestment += it[rowIndex].priceTarget * it[rowIndex].volume
                    "باز"
                } else "بسته"

                val rrr =
                    (it[rowIndex].priceTarget - it[rowIndex].enterPrice) / (it[rowIndex].enterPrice - it[rowIndex].sl)
                ttvm.cells.add(
                    mutableListOf(
                        Cell(it[rowIndex].symbol),
                        Cell(state),
                        Cell(currentPrice.toString().toCurrency() ?: "خطا در اتصال"),
                        Cell(enterPrice.toString().toCurrency()!!),
                        Cell("${"%.2f".format(percent)}%"),
                        Cell(it[rowIndex].enterDate.toPersianString()),
                        Cell(it[rowIndex].volume.toString().toCurrency()!!),
                        Cell(it[rowIndex].sl.toString().toCurrency()!!),
                        Cell(it[rowIndex].priceTarget.toString().toCurrency()!!),
                        Cell(it[rowIndex].dateTarget?.toPersianString()!!),
                        Cell("${"%.2f".format(rrr)}"),
                        Cell(it[rowIndex].sellPrice?.toString()?.toCurrency() ?: "-"),
                        Cell(it[rowIndex].sellDate?.toPersianString() ?: "-")
                    )
                )
            }

            _wr.postValue(((it.size - loss).toFloat() / it.size) * 100)
            _targetInvestment.postValue(targetCalculateInvestment)
            ttvm
        }
    }.asLiveData(viewModelScope.coroutineContext)

    private val _trades: MutableLiveData<LiveData<TradesTableViewModel>> by lazy {
        MutableLiveData<LiveData<TradesTableViewModel>>()
    }
    val trades: LiveData<LiveData<TradesTableViewModel>>
        get() = _trades

    private val _periodName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val periodName: LiveData<String>
        get() = _periodName

    private val _initialInvestment: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val initialInvestment: LiveData<Double>
        get() = _initialInvestment

    private val _nMax: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val nMax: LiveData<Int>
        get() = _nMax


    private val _wr: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }
    val wr: LiveData<Float>
        get() = _wr

    private val _closed: MutableLiveData<Unit> by lazy {
        MutableLiveData<Unit>()
    }
    val closed: LiveData<Unit>
        get() = _closed

    private val _targetInvestment: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val targetInvestment: LiveData<Double>
        get() = _targetInvestment
    // Models


}