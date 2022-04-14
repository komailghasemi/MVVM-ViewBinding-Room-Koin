package com.trader.note.view.tradingPeriod

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trader.note.model.dao.TradingPeriodDao
import com.trader.note.model.tables.TradingPeriod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTreadingPeriodViewModel(private val tradingPeriodDao: TradingPeriodDao) : ViewModel() {

    private val _name: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    private val _nameError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val nameError: LiveData<String>
        get() = _nameError

    private val _initialInvestment: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
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

    fun nameEvent(name: String?) {
        _name.value = name
    }

    fun initialInvestmentEvent(name: String?) {
        _initialInvestment.value = name?.toDoubleOrNull()
    }

    fun mddEvent(value: Float) {
        _mdd.value = value.toInt()
        calculate()
    }

    fun mclEvent(value: Float) {
        _mcl.value = value.toInt()
        calculate()
    }

    private fun calculate() {
        _rpt.value = (_mdd.value ?: 20).toFloat() / (_mcl.value ?: 2)
        _nMax.value = ((_mdd.value ?: 20) / (rpt.value ?: 10.0f)).toInt()
    }


    fun onSaveClicked() {
        if (validate()) {
            val name = _name.value!!
            val ii = _initialInvestment.value!!
            val mdd = _mdd.value ?: 20
            val mcl = _mcl.value ?: 2
            insert(TradingPeriod(name, ii, mdd, mcl)).invokeOnCompletion {
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

    private fun insert(tradingPeriod: TradingPeriod) = viewModelScope.launch(Dispatchers.IO) {
        tradingPeriodDao.insert(tradingPeriod)
    }
}