package com.xiang.sample.diycameralibrary.engine

class CameraFpsCounter {
    private var mUpdateTime = 0L
    private var mCount = 0
    private var mFps = 0f
    private var TIME_INTERVAL = 1000

    fun calculateFps() {
        val time = System.currentTimeMillis()
        if (mUpdateTime == 0L) {
            mUpdateTime = time
        }

        if (time - mUpdateTime > TIME_INTERVAL) {
            mFps = mCount / (time - mUpdateTime).toFloat() * 1000
            mUpdateTime = time
            mCount = 0
        }
        mCount++
    }

    fun getFps(): Float {
        return mFps
    }

    fun reset() {
        mCount = 0
        mUpdateTime = 0L
        mFps = 0f
    }
}