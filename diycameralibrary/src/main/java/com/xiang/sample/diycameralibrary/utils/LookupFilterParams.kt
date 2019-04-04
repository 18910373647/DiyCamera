package com.xiang.sample.diycameralibrary.utils

object LookupFilterParams {
    private var mLookupFilterIndex = 0
    private var isLookupFilterLeftScroll = false
    private var mLookupFilterScrollOffset = 0f
    private var mLookupFilterScrollState = 0
    private var mLookupFilterCount = 0
    private var mLookupFilterIntensity = 0f

    fun setLookupFilterCount(count: Int) {
        mLookupFilterCount = count
    }

    fun getLookupFilterCount(): Int {
        return mLookupFilterCount
    }

    fun onLookupFilterChanged(index: Int) {
        mLookupFilterIndex = index
    }

    fun getLookupFilterIndex(): Int {
        return mLookupFilterIndex
    }

    fun onLookupFilterOffset(isLeft: Boolean, offset: Float) {
        isLookupFilterLeftScroll = isLeft
        mLookupFilterScrollOffset = offset
    }

    fun isLookupFilterLeftScroll(): Boolean {
        return isLookupFilterLeftScroll
    }

    fun getLookupFilterScrollOffset(): Float {
        return mLookupFilterScrollOffset
    }

    fun onLookupFilterScrollState(state: Int) {
        mLookupFilterScrollState = state
    }

    fun getLookupFilterScrollState(): Int {
        return mLookupFilterScrollState
    }

    fun onLookupFilterIntensityChanged(intensity: Float) {
        mLookupFilterIntensity = intensity
    }

    fun getLookupFilterIntensity(): Float {
        return mLookupFilterIntensity
    }
}