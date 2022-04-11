package com.trader.note.view.tradeInitInformation


import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SeekBar
import com.trader.note.databinding.ActivityTradeInitInfoBinding
import com.trader.note.view.UI
import org.koin.androidx.viewmodel.ext.android.viewModel

class TradeInitInfoActivity : UI<ActivityTradeInitInfoBinding>() {
    private val vm : TradeInitInfoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.setBindingInflater(ActivityTradeInitInfoBinding.inflate(LayoutInflater.from(this)))
        super.onCreate(savedInstanceState)
        event()
    }
    private fun event(){
        binding.skbMDD.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.txtMDDPercent.text = "$progress%"
               calculate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.skbMCL.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.txtMCL.text = "$progress"
                binding.skbMDD.min = progress
                calculate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun calculate(){
        val rpt = binding.skbMDD.progress / binding.skbMCL.progress
        binding.txtRPT.text = "مهم : درصد ریسک (R.P.T) در هر معامله برابر است با $rpt درصد"
        val maxN = binding.skbMDD.progress / rpt
        binding.txtMaxN.text = "مهم : تعداد مجاز معاملات باز به صورت همزمان برابر است با $maxN"
    }
}