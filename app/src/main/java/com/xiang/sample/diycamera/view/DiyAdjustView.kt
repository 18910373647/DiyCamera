package com.xiang.sample.diycamera.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.RadioGroup
import android.widget.SeekBar
import com.xiang.sample.diycamera.R
import com.xiang.sample.diycameralibrary.utils.LookupFilterParams
import com.xiang.sample.diycameralibrary.utils.SpecialEffectFilterParams
import kotlinx.android.synthetic.main.diy_adjust_view_layout.view.*

class DiyAdjustView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, def: Int = 0):
    ConstraintLayout(context, attr, def) {
    private var mCheckedRadioButtonId = -1

    init {
        LayoutInflater.from(context).inflate(R.layout.diy_adjust_view_layout, this)
        initView()
        initEvent()
    }

    private fun initView() {
        radio_group.check(R.id.lookup_filter_radio)
        LookupFilterParams.onLookupFilterIntensityChanged(1f)
        mCheckedRadioButtonId = R.id.lookup_filter_radio
        seek_bar.max = 100
        seek_bar.progress = 100
    }

    private fun initEvent() {
        radio_group.setOnCheckedChangeListener(object: RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                mCheckedRadioButtonId = group?.checkedRadioButtonId ?: -1

                when (mCheckedRadioButtonId) {
                    R.id.lookup_filter_radio -> seek_bar.progress = (LookupFilterParams.getLookupFilterIntensity() * seek_bar.max).toInt()
                    R.id.gaussian_blur_radio -> seek_bar.progress = (SpecialEffectFilterParams.getBlurIntensity() * seek_bar.max).toInt()
                }
            }
        })

        seek_bar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val current = progress / seekBar.max.toFloat()

                when (mCheckedRadioButtonId) {
                    R.id.lookup_filter_radio -> LookupFilterParams.onLookupFilterIntensityChanged(current)
                    R.id.gaussian_blur_radio -> SpecialEffectFilterParams.onBlurIntensityChanged(current)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }
}