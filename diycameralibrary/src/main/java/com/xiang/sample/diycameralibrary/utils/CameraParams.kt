package com.xiang.sample.diycameralibrary.utils

import android.hardware.Camera
import com.xiang.sample.diycameralibrary.interfaces.ICameraFpsListener
import com.xiang.sample.diycameralibrary.interfaces.ICameraListener

class CameraParams private constructor() {
    // camera当前宽高比
    var mCameraRatio = CAMERA_RATIO_4_3
    // camera id
    var mCameraId = CAMERA_DEFAULT_ID
    // camera 帧率
    var mCameraFps = CAMERA_DEFAULT_FPS
    // camera 是否支持闪光灯
    var isCameraSupportFlash: Boolean = false
    // camera 预览宽高(分辨率)
    var mPreviewWidth: Int = 0
    var mPreviewHeight: Int = 0
    // camera surface view宽高
    var mViewWidth: Int = 0
    var mViewHeight: Int = 0
    // camera rotation
    var mCameraRotation = 0
    // camera focus mode
    var mCameraFocusMode = ""
    // 是否显示帧率
    var isShowCameraFps = false

    var mCameraListener: ICameraListener? = null
    var mCameraFpsListener: ICameraFpsListener? = null

    companion object {
        val instance: CameraParams by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            CameraParams()
        }

        // camera默认帧率
        val CAMERA_DEFAULT_FPS = 30
        // camera默认id
        var CAMERA_DEFAULT_ID = Camera.CameraInfo.CAMERA_FACING_FRONT

        // camera 16:9 的默认宽高
        val CAMERA_DEFAULT_16_9_WIDTH =  720
        val CAMERA_DEFAULT_16_9_HEIGHT = 1280
        // camera 4:3 的默认宽高
        val CAMERA_DEFAULT_4_3_WIDTH = 720
        val CAMERA_DEFAULT_4_3_HEIGHT = 960
        val CAMERA_RATIO_4_3 = 3f / 4
        val CAMERA_RATIO_16_9 = 9f / 16
    }

    fun setCameraListener(listener: ICameraListener) {
        mCameraListener = listener
    }

    fun setCameraFpsListener(listener: ICameraFpsListener) {
        mCameraFpsListener = listener
    }

    fun clear() {
        mCameraListener = null
        mCameraFpsListener = null
    }
}