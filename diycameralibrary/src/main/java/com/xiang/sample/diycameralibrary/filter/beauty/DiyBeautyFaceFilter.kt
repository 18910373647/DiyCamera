package com.xiang.sample.diycameralibrary.filter.beauty

import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import com.xiang.sample.diycameralibrary.filter.gaussian.DiyGaussianBlurFilter
import java.nio.FloatBuffer

/**
 * 磨皮
 */
class DiyBeautyFaceFilter: BaseFilter() {
    private val mGaussianBlurFilter: DiyGaussianBlurFilter by lazy { DiyGaussianBlurFilter() }
    private val mHighPassFilter: HighPassFilter by lazy { HighPassFilter() }
    private val mGaussianBlurFilter1: DiyGaussianBlurFilter by lazy { DiyGaussianBlurFilter() }
    private val mSkinAdjustFilter: SkinAdjustFilter by lazy { SkinAdjustFilter() }

    override fun onDrawFrameBuffer(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer): Int {
        mGaussianBlurFilter.setBlurIntensity(1f)
        val blurTexture = mGaussianBlurFilter.drawFrameBuffer(textureId, vertexFb, fragmentFb)
        mHighPassFilter.mBlurTexture = blurTexture
        val highPassTexture = mHighPassFilter.drawFrameBuffer(textureId, vertexFb, fragmentFb)
        mGaussianBlurFilter1.setBlurIntensity(1f)
        val highPassBlurTexture = mGaussianBlurFilter1.drawFrameBuffer(highPassTexture, vertexFb, fragmentFb)
        mSkinAdjustFilter.mBlurTexture = blurTexture
        mSkinAdjustFilter.mHighPassBlurTexture = highPassBlurTexture
        return mSkinAdjustFilter.drawFrameBuffer(textureId, vertexFb, fragmentFb)
    }
}