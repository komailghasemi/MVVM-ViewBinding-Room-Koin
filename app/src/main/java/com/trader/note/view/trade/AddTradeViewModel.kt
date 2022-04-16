package com.trader.note.view.trade


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trader.note.coins.Coin
import com.trader.note.model.dao.TradeDao
import com.trader.note.model.dao.TradingPeriodDao
import com.trader.note.model.tables.Trade
import com.trader.note.model.tables.TradingPeriod
import com.trader.note.utils.round
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class AddTradeViewModel(
    private val tradeDao: TradeDao,
    private val tradingPeriodDao: TradingPeriodDao,
    private val coin: Coin
) : ViewModel() {


    fun viewCreated(periodId: Int, tradeId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            period = tradingPeriodDao.getById(periodId)
            _symbols.postValue(coin.getCoinList())
            this@AddTradeViewModel.tradeId = if (tradeId == -1) null else tradeId
            loadTrade()
        }
    }

    private fun loadTrade() {
        if (tradeId != null) {
            val trade = tradeDao.getById(tradeId!!)
            viewModelScope.launch(Dispatchers.Main) {
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

    fun setDescription(desc: String?) {
        _description.value = desc
    }

    fun setVolume(vol: String?) {
        _volume.value = vol?.toDoubleOrNull()
        calculateSarBeSar()
    }

    fun setBuyCommission(bc: String?) {
        _buyCommission.value = bc?.toDoubleOrNull()
        calculateVolume()
        calculateSarBeSar()
    }

    fun setSellCommission(sc: String?) {
        _sellCommission.value = sc?.toDoubleOrNull()
        calculateVolume()
        calculateSarBeSar()
    }

    fun setTargetDate(ms: Long) {
        _targetDate.value = ms
    }

    fun setTargetPrice(targetPrice: String?) {
        _targetPrice.value = targetPrice?.toDoubleOrNull()
        calculateRRR()
    }

    fun setSl(sl: String?) {
        _sl.value = sl?.toDoubleOrNull()
        calculateVolume()
        calculateRRR()
    }

    fun setEnterPrice(price: String?) {
        _enterPrice.value = price?.toDoubleOrNull()
        calculateVolume()
        calculateRRR()
        calculateSarBeSar()
    }

    fun setSellPrice(price: String?) {
        if (price != null)
            _sellPrice.value = price.toDouble()
    }

    fun setSellDate(ms: Long?) {
        if (ms != null)
            _sellDate.value = ms!!
    }

    fun setSymbol(symbol: String?) {
        _symbol.value = symbol
    }

    private fun calculateVolume() {
        // R2 = RPT
        // U2 = investment
        // K2 = buy price
        // J2 = SL
        // T2_1 = sell Commission
        // T2_2 = buy Commission

        val R2 = (period.MDD / period.MCL).toDouble()
        val U2 = period.initialInvestment
        val K2 = (_enterPrice.value ?: 0.0).toDouble()
        val J2 = (_sl.value ?: 0.0).toDouble()
        val T2_1 = (_sellCommission.value ?: 0.0).toDouble()
        val T2_2 = (_buyCommission.value ?: 0.0).toDouble()


        val A = ((R2 / 100) * U2)
        val B = ((K2 - J2) + ((J2 * T2_1) + (K2 * T2_2)))
        if (B == 0.0) {
            _slError.value = "حد ضرر نمیتواند برابر یا بیشتر از مبلغ خرید باشد"
            _volumeHelper.value = 0.0
        } else {
            val vol = A / B
            _volumeHelper.value = vol.round().toDouble()
        }


    }

    private fun calculateRRR() {

        // I2 = Target Price
        // K2 = Buy Price
        // J2 = sl

        val I2 = (_targetPrice.value ?: 0.0).toDouble()
        val K2 = (_enterPrice.value ?: 0.0).toDouble()
        val J2 = (_sl.value ?: 0.0).toDouble()

        // (I2 - K2) / (K2 - J2)

        val A = (I2 - K2)
        val B = (K2 - J2)

        if (B == 0.0) {
            _slError.value = "حد ضرر نمیتواند برابر یا بیشتر از مبلغ خرید باشد"
            _rrr.value = 0.0
        } else {
            val rrr = A / B
            _rrr.value = rrr.round().toDouble()
        }
    }

    private fun calculateSarBeSar() {
        // K2 = Buy Price
        // F2 = volume
        // T2_1 = sell Commission
        // T2_2 = buy Commission

        val F2 = (_volume.value ?: 0.0).toDouble()
        val K2 = (_enterPrice.value ?: 0.0).toDouble()
        val T2_1 = (_sellCommission.value ?: 0.0).toDouble()
        val T2_2 = (_buyCommission.value ?: 0.0).toDouble()

        //( K2 * F2 / F2) + (T2_1 + T2_2)

        if (F2 != 0.0) {
            val sarBeSar = (K2 * F2 / F2) + (T2_1 + T2_2)
            _sarBeSar.value = sarBeSar
        } else
            _sarBeSar.value = 0.0
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
                    period.uid!!,
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
                    uid = tradeId
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

        if (trade.uid == null) {
            tradeId = tradeDao.insert(trade).toInt()
        } else
            tradeDao.update(trade)
    }


    // Models
    private lateinit var period: TradingPeriod
    private var tradeId: Int? = null

    private val _symbols: MutableLiveData<List<String>> by lazy {
        MutableLiveData()
    }
    val symbols: LiveData<List<String>>
        get() = _symbols

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

    private val _enterPrice: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val enterPrice: LiveData<Double>
        get() = _enterPrice

    private val _enterPriceError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val enterPriceError: LiveData<String>
        get() = _enterPriceError

    private val _sl: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val sl: LiveData<Double>
        get() = _sl

    private val _slError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val slError: LiveData<String>
        get() = _slError

    private val _targetPrice: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val targetPrice: LiveData<Double>
        get() = _targetPrice

    private val _targetDate: MutableLiveData<Long> = MutableLiveData<Long>().apply {
        value = System.currentTimeMillis()
    }

    val targetDate: LiveData<Long>
        get() = _targetDate

    private val _buyCommission: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val buyCommission: LiveData<Double>
        get() = _buyCommission

    private val _sellCommission: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val sellCommission: LiveData<Double>
        get() = _sellCommission

    private val _volume: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val volume: LiveData<Double>
        get() = _volume

    private val _volumeError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val volumeError: LiveData<String>
        get() = _volumeError

    private val _volumeHelper: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val volumeHelper: LiveData<Double>
        get() = _volumeHelper

    private val _rrr: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val rrr: LiveData<Double>
        get() = _rrr

    private val _sarBeSar: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }
    val sarBeSar: LiveData<Double>
        get() = _sarBeSar

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