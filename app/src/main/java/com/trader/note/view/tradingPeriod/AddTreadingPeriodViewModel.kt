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

    fun nameEvent(name: String?) {
        _name.value = name
    }

    private val _initialInvestment: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    private val _initialInvestmentError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val initialInvestmentError: LiveData<String>
        get() = _initialInvestmentError

    fun initialInvestmentEvent(name: String?) {
        _initialInvestment.value = name?.toDouble()
    }

    private val _mdd: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }
    val mdd: LiveData<Float>
        get() = _mdd

    fun mddEvent(value: Float) {
        _mdd.value = value
        calculate()
    }

    private val _mcl: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>()
    }
    val mcl: LiveData<Float>
        get() = _mcl

    fun mclEvent(value: Float) {
        _mcl.value = value
        calculate()
    }

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

    private fun calculate() {
        _rpt.value = (_mdd.value ?: 20f) / (_mcl.value ?: 2f)
        _nMax.value = ((_mdd.value ?: 20f) / (rpt.value ?: 10.0f)).toInt()
    }


    private val _onSaved: MutableLiveData<Unit> by lazy {
        MutableLiveData<Unit>()
    }
    val onSaved: LiveData<Unit>
        get() = _onSaved

    fun onSaveClicked() {
        if (validate()) {
            val name = _name.value!!
            val ii = _initialInvestment.value!!
            val mdd = (_mdd.value ?: 20f).toInt()
            val mcl = (_mcl.value ?: 2f).toInt()
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