package com.xiang.sample.diycamera.application

import android.app.Application
import com.xiang.sample.diycamera.utils.FlipperHelper
import com.xiang.sample.globallibrary.DiyCameraKit


class DiyCameraApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        initDiyCameraKit()
        initFlipper()
    }

    private fun initDiyCameraKit() {
        DiyCameraKit.init(applicationContext)
    }

    private fun initFlipper() {
        FlipperHelper.instance.init()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}