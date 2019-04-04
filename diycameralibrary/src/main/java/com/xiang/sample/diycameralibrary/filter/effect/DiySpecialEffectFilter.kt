package com.xiang.sample.diycameralibrary.filter.effect

import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import com.xiang.sample.diycameralibrary.filter.effect.edgeblur.DiyEdgeBlurFilter
import com.xiang.sample.diycameralibrary.filter.effect.rain.DiyRainDropFilter
import com.xiang.sample.diycameralibrary.filter.effect.rain.DiyRainSlideFilter
import com.xiang.sample.diycameralibrary.utils.SpecialEffectFilterParams
import java.nio.FloatBuffer

class DiySpecialEffectFilter: BaseFilter() {
    private var mCurrentFilter: BaseFilter? = null
    private var mLastFilterIndex = -1

    private fun updateEffectFilter() {
        val filter = findCurrentFilter() ?: return
        releaseCurrentFilter()
        mCurrentFilter = filter
    }

    private fun findCurrentFilter(): BaseFilter? {
        val current = SpecialEffectFilterParams.getSpecialEffectIndex()
        if (mLastFilterIndex == current) {
            return null
        } else {
            mLastFilterIndex = current
        }

        return when(mLastFilterIndex) {
            1 -> DiyRainDropFilter()
            2 -> DiyRainSlideFilter()
            3 -> DiyEdgeBlurFilter()
            else -> BaseFilter()
        }
    }

    private fun releaseCurrentFilter() {
        mCurrentFilter?.release()
        mCurrentFilter = null
    }

    override fun onDrawFrameBuffer(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer): Int {
        updateEffectFilter()
        return mCurrentFilter?.drawFrameBuffer(textureId, vertexFb, fragmentFb) ?: textureId
    }

    override fun onDrawFrame(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer) {
        updateEffectFilter()
        mCurrentFilter?.drawFrame(textureId, vertexFb, fragmentFb)
    }

    override fun release() {
        super.release()
        releaseCurrentFilter()
    }
}