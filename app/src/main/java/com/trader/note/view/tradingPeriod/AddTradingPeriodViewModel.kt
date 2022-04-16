package com.trader.note.view.tradingPeriod

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trader.note.model.dao.TradingPeriodDao
import com.trader.note.model.tables.TradingPeriod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class AddTradingPeriodViewModel(private val tradingPeriodDao: TradingPeriodDao) : ViewModel() {

    fun viewCreated(periodId: Int = -1) {
        this.periodId = if (periodId == -1) null else periodId
        loadPeriod()
    }

    private fun loadPeriod() = viewModelScope.launch(Dispatchers.IO) {
        if (periodId != null) {
            val tp = tradingPeriodDao.getById(periodId!!)
            launch(Dispatchers.Main) {
                setName(tp.periodName)
                setInitialInvestment(tp.initialInvestment.toString())
                setMdd(tp.MDD.toFloat())
                setMcl(tp.MCL.toFloat())
                setStartDate(tp.startDate)
                if (tp.endDate == null)
                    _state.value = "EDIT"
                else
                    _state.value = "CLOSED"
            }
        } else
            _state.postValue("NEW")

    }

    private fun setStartDate(date : Date){
        startDate = date
    }

    fun setName(name: String?) {
        _name.value = name
    }

    fun setInitialInvestment(value: String?) {
        _initialInvestment.value = value?.toDoubleOrNull()
    }

    fun setMdd(value: Float) {
        _mdd.value = value.toInt()
        calculate()
    }

    fun setMcl(value: Float) {
        _mcl.value = value.toInt()
        calculate()
    }

    private fun setRpt(value: Float) {
        _rpt.value = value
    }

    private fun setNMax(value: Int) {
        _nMax.value = value
    }

    private fun calculate() {
        setRpt((_mdd.value ?: 20).toFloat() / (_mcl.value ?: 2))
        setNMax(((_mdd.value ?: 20) / (rpt.value ?: 10.0f)).toInt())
    }


    fun onSaveClicked(close: Boolean) {
        if (validate()) {
            val name = _name.value!!
            val ii = _initialInvestment.value!!
            val mdd = _mdd.value ?: 20
            val mcl = _mcl.value ?: 2
            var endDate = if (close) Date() else null

            upsert(
                TradingPeriod(
                    name,
                    ii,
                    mdd,
                    mcl,
                    startDate,
                    uid = periodId,
                    endDate = endDate
                )
            ).invokeOnCompletion {
                if (it == null)
                    _onSaved.postValue(Unit)
            }
        }
    }

    private fun validate(): Boolean {
        if (_name.value.isNullOrEmpty()) {
            _nameError.value = "وارد کردن نام الزامیست"
            return false
        }

        if (_initialInvestment.value?.toString().isNullOrEmpty()) {
            _initialInvestmentError.value = "وارد کردن سرمایه اولیه الزامیست"
            return false
        }
        return true
    }

    private fun upsert(tradingPeriod: TradingPeriod) = viewModelScope.launch(Dispatchers.IO) {
        if (tradingPeriod.uid == null){
            periodId = tradingPeriodDao.insert(tradingPeriod).toInt()
        }

        else
            tradingPeriodDao.update(tradingPeriod)

        if (tradingPeriod.endDate == null)
            _state.postValue("EDIT")
        else
            _state.postValue("CLOSED")
    }

    // Models

    private var periodId: Int? = null
    private var startDate: Date = Date()

    private val _state: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val state: LiveData<String>
        get() = _state

    private val _name: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val name: LiveData<String>
        get() = _name

    private val _nameError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val nameError: LiveData<String>
        get() = _nameError

    private val _initialInvestment: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val initialInvestment: LiveData<Double>
        get() = _initialInvestment

    private val _initialInvestmentError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val initialInvestmentError: LiveData<String>
        get() = _initialInvestmentError

    private val _mdd: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val mdd: LiveData<Int>
        get() = _mdd

    private val _mcl: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val mcl: LiveData<Int>
        get() = _mcl

    private val _rpt: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }
    val rpt: LiveData<Float>
        get() = _rpt

    private val _nMax: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val nMax: LiveData<Int>
        get() = _nMax

    private val _onSaved: MutableLiveData<Unit> by lazy {
        MutableLiveData<Unit>()
    }
    val onSaved: LiveData<Unit>
        get() = _onSaved
    // Models
}