package com.xiang.sample.diycameralibrary.utils

object SpecialEffectFilterParams {
    private var mBlurIntensity: Float = 0f
    private var mSpecialEffectIndex = 0

    fun onBlurIntensityChanged(intensity: Float) {
        mBlurIntensity = intensity
    }

    fun getBlurIntensity(): Float {
        return mBlurIntensity
    }

    fun onSpecialEffectChanged(index: Int) {
        mSpecialEffectIndex = index
    }

    fun getSpecialEffectIndex(): Int {
        return mSpecialEffectIndex
    }
}