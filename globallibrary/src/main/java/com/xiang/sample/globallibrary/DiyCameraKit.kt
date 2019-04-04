package com.xiang.sample.globallibrary

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat

/**
 * 全局配置，生命周期跟随Application
 */
object DiyCameraKit {
    private var mContext: Context? = null // Application Context

    // application 中初始化，而且只能在application中初始化，否则可能leak
    fun init(context: Context) {
        mContext = context
    }

    fun getAppContext(): Context {
        return mContext!!
    }

    fun getPixels(dip: Float): Int {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, mContext!!.resources.displayMetrics))
    }

    fun getScrreenWidth() {

    }

    fun timeToString(): String {
        val sdf = SimpleDateFormat("MM-dd HH:mm")
        return sdf.format(System.currentTimeMillis())
    }


}