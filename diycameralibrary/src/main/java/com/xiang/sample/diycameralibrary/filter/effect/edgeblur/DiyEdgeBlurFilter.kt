package com.xiang.sample.diycameralibrary.filter.effect.edgeblur

import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import com.xiang.sample.diycameralibrary.filter.gaussian.DiyGaussianBlurFilter
import java.nio.FloatBuffer

class DiyEdgeBlurFilter: BaseFilter() {
    private val mGaussianBlurFilter: DiyGaussianBlurFilter by lazy { DiyGaussianBlurFilter() }
    private val mEdgeBlurFilter: EdgeBlurFilter by lazy { EdgeBlurFilter() }

    override fun onDrawFrameBuffer(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer): Int {
        mGaussianBlurFilter.setBlurIntensity(1f)
        val blurTexture = mGaussianBlurFilter.drawFrameBuffer(textureId, vertexFb, fragmentFb)

        mEdgeBlurFilter.mBlurTexture = blurTexture
        return mEdgeBlurFilter.drawFrameBuffer(textureId, vertexFb, fragmentFb)
    }
}