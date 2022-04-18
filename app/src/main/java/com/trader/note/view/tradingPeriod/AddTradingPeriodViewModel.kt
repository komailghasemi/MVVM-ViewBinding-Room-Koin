package com.trader.note.view.tradingPeriod

import androidx.lifecycle.*
import com.trader.note.model.repo.TradingPeriodRepository
import com.trader.note.model.tables.TradingPeriod
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*

sealed class State {
    object Edit : State()
    object Closed : State()
    object New : State()
}

class AddTradingPeriodViewModel(
    private var periodId: Int,
    private val tradingPeriodRepository: TradingPeriodRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            tradingPeriodRepository.getById(periodId).collect { tp ->
                if (tp != null) {
                    setName(tp.periodName)
                    setInitialInvestment(tp.initialInvestment.toString())
                    setMdd(tp.MDD.toFloat())
                    setMcl(tp.MCL.toFloat())
                    setDescription(tp.description)
                    startDate = tp.startDate
                    if (tp.endDate == null)
                        _state.value = State.Edit
                    else
                        _state.value = State.Closed
                } else if (periodId == -1)
                    _state.value = State.New
            }

        }
    }


    fun setName(name: String?) {
        _name.value = name
    }

    fun setDescription(desc: String?) {
        _description.value = desc
    }

    fun setInitialInvestment(value: String?) {
        _initialInvestment.value = value?.toDoubleOrNull()
    }

    fun setMdd(value: Float) {
        _mdd.value = value.toInt()
    }

    fun setMcl(value: Float) {
        _mcl.value = value.toInt()
    }


    fun onSaveClicked(close: Boolean) {
        if (validate()) {
            val name = _name.value!!
            val ii = _initialInvestment.value!!
            val desc = _description.value
            val mdd = _mdd.value ?: 20
            val mcl = _mcl.value ?: 2
            val endDate = if (close) Date() else null

            upsert(
                TradingPeriod(
                    name,
                    ii,
                    mdd,
                    mcl,
                    description = desc,
                    startDate = startDate ?: Date(),
                    uid = if (periodId == -1) null else periodId,
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

    private fun upsert(tradingPeriod: TradingPeriod) = viewModelScope.launch {
        periodId = tradingPeriodRepository.upsert(tradingPeriod)?.toInt() ?: -1

        if (tradingPeriod.endDate == null)
            _state.value = State.Edit
        else
            _state.value = State.Closed
    }

    // Models

    private var startDate: Date? = null

    private val _state: MutableLiveData<State> by lazy {
        MutableLiveData<State>()
    }
    val state: LiveData<State>
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

    private val _description: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val description: LiveData<String>
        get() = _description


    private val _mdd: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(20)
    }
    val mdd: LiveData<Int>
        get() = _mdd

    private val _mcl: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(2)
    }
    val mcl: LiveData<Int>
        get() = _mcl

    val rpt: LiveData<Float>
        get() = combine(mdd.asFlow(), mcl.asFlow()) { mdd, mcl ->
            mdd.toFloat() / mcl
        }.asLiveData()

    val nMax: LiveData<Int>
        get() = combine(mdd.asFlow(), rpt.asFlow()) { mdd, rpt ->
            (mdd / rpt).toInt()
        }.asLiveData()

    private val _onSaved: MutableLiveData<Unit> by lazy {
        MutableLiveData<Unit>()
    }
    val onSaved: LiveData<Unit>
        get() = _onSaved
    // Models
}