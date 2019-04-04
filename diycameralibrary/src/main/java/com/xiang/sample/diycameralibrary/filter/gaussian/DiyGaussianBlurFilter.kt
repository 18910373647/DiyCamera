package com.xiang.sample.diycameralibrary.filter.gaussian

import com.xiang.sample.diycameralibrary.filter.base.GroupFilter
import com.xiang.sample.diycameralibrary.utils.CameraParams

class DiyGaussianBlurFilter: GroupFilter() {
    private val mVerticalGaussianBlurFilter: GaussianBlurFilter by lazy { GaussianBlurFilter() }
    private val mHorizontalGaussianBlurFilter: GaussianBlurFilter by lazy { GaussianBlurFilter() }
    private var mIntensity = 0f

    init {
        addFilter(mVerticalGaussianBlurFilter)
        addFilter(mHorizontalGaussianBlurFilter)
    }

    fun setBlurIntensity(intensity: Float) {
        mIntensity = intensity
    }

    override fun passShaderValue() {
        super.passShaderValue()

        val width = CameraParams.instance.mPreviewWidth
        val height = CameraParams.instance.mPreviewHeight

        mVerticalGaussianBlurFilter.setBlurSize(mIntensity)
        mVerticalGaussianBlurFilter.setTexelOffset(0f, height.toFloat())

        mHorizontalGaussianBlurFilter.setBlurSize(mIntensity)
        mHorizontalGaussianBlurFilter.setTexelOffset(width.toFloat(), 0f)

    }
}