package com.xiang.sample.diycameralibrary.filter.base

import java.nio.FloatBuffer

abstract class GroupFilter: BaseFilter() {
    private val mFilterList = mutableListOf<BaseFilter>()
    private val mMergedFilterList = mutableListOf<BaseFilter>()

    fun addFilter(filter: BaseFilter) {
        if (!mFilterList.contains(filter)) {
            mFilterList.add(filter)
        }
        updateMergedFilter()
    }

    fun removeFilter(filter: BaseFilter) {
        if (mFilterList.contains(filter)) {
            mFilterList.remove(filter)
        }
        updateMergedFilter()
    }

    fun getFilterList(): MutableList<BaseFilter> {
        return mFilterList
    }

    fun getMergedFilterList(): MutableList<BaseFilter> {
        return mMergedFilterList
    }

    private fun updateMergedFilter() {
        mMergedFilterList.clear()

        var subMergedFilterList: MutableList<BaseFilter>
        val size = mFilterList.size

        for (index in 0 until size) {
            val filter = mFilterList[index]

            if (filter is GroupFilter) {
                filter.updateMergedFilter()
                subMergedFilterList = filter.getMergedFilterList()
                if (subMergedFilterList.isEmpty()) {
                    continue
                } else {
                    mMergedFilterList.addAll(subMergedFilterList)
                }
            }
            mMergedFilterList.add(filter)
        }
    }

    override fun onDrawFrameBuffer(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer): Int {
        passShaderValue()
        var currentTextureId = textureId
        mMergedFilterList.forEach {
            currentTextureId = it.drawFrameBuffer(currentTextureId, vertexFb, fragmentFb)
        }
        return currentTextureId
    }

    override fun onDrawFrame(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer) {
        var currentTextureId = textureId

        val size = mMergedFilterList.size
        for (index in 0 until size - 1) {
            currentTextureId = mMergedFilterList[index].drawFrameBuffer(currentTextureId, vertexFb, fragmentFb)
        }
        mMergedFilterList[size - 1].drawFrame(currentTextureId, vertexFb, fragmentFb)
    }

    override fun release() {
        super.release()

        mFilterList.forEach {
            it.release()
        }
    }
}