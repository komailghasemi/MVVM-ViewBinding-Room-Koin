package com.trader.note.view.trade


import androidx.lifecycle.*
import com.trader.note.coins.Coin
import com.trader.note.model.repo.TradeRepository
import com.trader.note.model.repo.TradingPeriodRepository
import com.trader.note.model.tables.Trade
import com.trader.note.utils.round
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*

class AddTradeViewModel(
    private var periodId: Int,
    private var tradeId: Long,
    private val tradeRepository: TradeRepository,
    private val tradingPeriodRepository: TradingPeriodRepository,
    private val coin: Coin
) : ViewModel() {

    init {
        viewModelScope.launch {
            tradingPeriodRepository.getById(periodId).collect {
                mdd = it?.MDD ?: 0
                mcl = it?.MCL ?: 0
                initialInvestment = it?.initialInvestment ?: 0.0

                tradeRepository.getById(tradeId.toInt()).collect { trade ->
                    if (trade != null) {
                        setSymbol(trade.symbol)
                        setEnterPrice(trade.enterPrice.toString())
                        setSl(trade.sl.toString())
                        setTargetPrice(trade.priceTarget.toString())
                        setTargetDate(trade.dateTarget?.time ?: System.currentTimeMillis())
                        setBuyCommission(trade.buy_commission.toString())
                        setSellCommission(trade.sell_commission.toString())
                        setVolume(trade.volume.toString())
                        setDescription(trade.description)
                        setSellDate(trade.sellDate?.time)
                        setSellPrice(trade.sellPrice?.toString())
                        if (trade.sellPrice != null) {
                            _closed.value = Unit
                        }
                    }
                }
            }
        }
    }

    fun setDescription(desc: String?) {
        _description.value = desc
    }

    fun setVolume(vol: String?) {
        _volume.value = vol?.toDoubleOrNull()
    }

    fun setBuyCommission(bc: String?) {
        _buyCommission.value = bc?.toDoubleOrNull()
    }

    fun setSellCommission(sc: String?) {
        _sellCommission.value = sc?.toDoubleOrNull()
    }

    fun setTargetDate(ms: Long) {
        _targetDate.value = ms
    }

    fun setTargetPrice(targetPrice: String?) {
        _targetPrice.value = targetPrice?.toDoubleOrNull()
    }

    fun setSl(sl: String?) {
        _sl.value = sl?.toDoubleOrNull()
    }

    fun setEnterPrice(price: String?) {
        _enterPrice.value = price?.toDoubleOrNull()
    }

    fun setSellPrice(price: String?) {
        if (price != null)
            _sellPrice.value = price.toDouble()
    }

    fun setSellDate(ms: Long?) {
        ms?.let { _sellDate.value = it }
    }

    fun setSymbol(symbol: String?) {
        _symbol.value = symbol
    }


    fun onSaveClicked() {
        if (validate()) {
            val symbol = _symbol.value!!
            val enterDate = Date()
            val enterPrice = _enterPrice.value!!
            val vol = _volume.value!!
            val sl = _sl.value!!
            val priceTarget = _targetPrice.value ?: enterPrice
            val buyCommission = _buyCommission.value ?: 0.0
            val sellCommission = _sellCommission.value ?: 0.0
            val dateTarget = Date(_targetDate.value ?: System.currentTimeMillis())
            val description = _description.value

            val closePrice = _sellPrice.value
            if (closePrice != null) {
                _closed.value = Unit
            }
            val closeDate = if (closePrice != null) {
                Date(_sellDate.value ?: System.currentTimeMillis())
            } else null
            upsert(
                Trade(
                    periodId,
                    symbol,
                    enterDate,
                    enterPrice,
                    vol,
                    sl,
                    priceTarget,
                    buyCommission,
                    sellCommission,
                    dateTarget,
                    sellDate = closeDate,
                    sellPrice = closePrice,
                    description = description,
                    uid = if (tradeId == -1L) null else tradeId.toInt()
                )
            ).invokeOnCompletion {
                if (it == null) {
                    if (closePrice != null)
                        _closed.postValue(Unit)
                    _onSaved.postValue(Unit)
                }

            }
        }
    }


    private fun validate(): Boolean {
        if (_symbol.value.isNullOrEmpty()) {
            _symbolError.value = "وارد کردن ارز دیجیتال الزامیست"
            return false
        }

        if (_enterPrice.value?.toString().isNullOrEmpty()) {
            _enterPriceError.value = "وارد کردن قیمت خرید الزامیست"
            return false
        }

        if (_sl.value?.toString().isNullOrEmpty()) {
            _slError.value = "وارد کردن حد ضرر الزامیست"
            return false
        }

        if (_sl.value?.toString() == _enterPrice.value?.toString()) {
            _slError.value = "حد ضرر نمیتواند برابر یا بیشتر از مبلغ خرید باشد"
            return false
        }

        if (_volume.value?.toString().isNullOrEmpty()) {
            _volumeError.value = "وارد کردن حجم خرید الزامیست"
            return false
        }
        return true
    }


    private fun upsert(trade: Trade) = viewModelScope.launch(Dispatchers.IO) {
        tradeId = tradeRepository.upsert(trade) ?: -1L
    }


    // Models

    private var mdd: Int = 0
    private var mcl: Int = 0
    private var initialInvestment: Double = 0.0


    val symbols: LiveData<List<String>>
        get() = coin.getCoinList().asLiveData()

    private val _symbolError: MutableLiveData<String> by lazy {
        MutableLiveData()
    }
    val symbolError: LiveData<String>
        get() = _symbolError

    private val _symbol: MutableLiveData<String> by lazy {
        MutableLiveData()
    }
    val symbol: LiveData<String>
        get() = _symbol

    private val _enterPrice: MutableLiveData<Double?> by lazy {
        MutableLiveData<Double?>()
    }
    val enterPrice: LiveData<Double?>
        get() = _enterPrice

    private val _enterPriceError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val enterPriceError: LiveData<String>
        get() = _enterPriceError

    private val _sl: MutableLiveData<Double?> by lazy {
        MutableLiveData<Double?>()
    }
    val sl: LiveData<Double?>
        get() = _sl

    private val _slError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val slError: LiveData<String>
        get() = _slError

    private val _targetPrice: MutableLiveData<Double?> by lazy {
        MutableLiveData<Double?>()
    }
    val targetPrice: LiveData<Double?>
        get() = _targetPrice

    private val _targetDate: MutableLiveData<Long> = MutableLiveData<Long>().apply {
        value = System.currentTimeMillis()
    }

    val targetDate: LiveData<Long>
        get() = _targetDate

    private val _buyCommission: MutableLiveData<Double?> by lazy {
        MutableLiveData<Double?>(0.0)
    }
    val buyCommission: LiveData<Double?>
        get() = _buyCommission

    private val _sellCommission: MutableLiveData<Double?> by lazy {
        MutableLiveData<Double?>(0.0)
    }
    val sellCommission: LiveData<Double?>
        get() = _sellCommission

    private val _volume: MutableLiveData<Double?> by lazy {
        MutableLiveData<Double?>()
    }
    val volume: LiveData<Double?>
        get() = _volume

    private val _volumeError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val volumeError: LiveData<String>
        get() = _volumeError


    val volumeHelper: LiveData<Double>
        get() = combine(
            enterPrice.asFlow(),
            sl.asFlow(),
            sellCommission.asFlow(),
            buyCommission.asFlow()
        ) { enter, sl, sCom, bCom ->
            val rpt = (mdd / mcl).toDouble()
            val A = ((rpt / 100) * initialInvestment)
            val B =
                (((enter ?: 0.0) - (sl ?: 0.0)) + (((sl ?: 0.0) * (sCom ?: 0.0)) + ((enter ?: 0.0) * (bCom ?: 0.0))))
            if (B == 0.0) {
                _slError.value = "حد ضرر نمیتواند برابر یا بیشتر از مبلغ خرید باشد"
                0.0
            } else {
                val vol = A / B
                vol.round().toDouble()
            }
        }.asLiveData()

    val rrr: LiveData<Double>
        get() = combine(
            targetPrice.asFlow(),
            enterPrice.asFlow(),
            sl.asFlow()
        ) { target, enter, sl ->

            val A = ((target ?: 0.0) - (enter ?: 0.0))
            val B = ((enter ?: 0.0) - (sl ?: 0.0))

            if (B == 0.0) {
                _slError.value = "حد ضرر نمیتواند برابر یا بیشتر از مبلغ خرید باشد"
                0.0
            } else {
                val rrr = A / B
                rrr.round().toDouble()
            }
        }.asLiveData()

    val sarBeSar: LiveData<Double>
        get() = combine(
            volume.asFlow(),
            enterPrice.asFlow(),
            sellCommission.asFlow(),
            buyCommission.asFlow()
        ) { volume, enter, sCom, bCom ->
            if (volume != null && volume != 0.0) {
                val sarBeSar = ((enter ?: 0.0) * volume / volume) + ((sCom ?: 0.0) + (bCom ?: 0.0))
                sarBeSar
            } else
                0.0
        }.asLiveData()

    private val _description: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val description: LiveData<String>
        get() = _description


    private val _sellPrice: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val sellPrice: LiveData<Double>
        get() = _sellPrice

    private val _closed: MutableLiveData<Unit> by lazy {
        MutableLiveData<Unit>()
    }
    val closed: LiveData<Unit>
        get() = _closed

    private val _sellDate: MutableLiveData<Long> = MutableLiveData<Long>().apply {
        value = System.currentTimeMillis()
    }

    val sellDate: LiveData<Long>
        get() = _sellDate

    private val _onSaved: MutableLiveData<Unit> by lazy {
        MutableLiveData<Unit>()
    }
    val onSaved: LiveData<Unit>
        get() = _onSaved
    // Models
}