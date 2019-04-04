package com.xiang.sample.diycameralibrary.engine

import android.app.Activity
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.view.SurfaceHolder
import com.xiang.sample.diycameralibrary.utils.CameraErrno
import com.xiang.sample.diycameralibrary.utils.CameraParams
import java.lang.ref.WeakReference

class PreviewRendererThread(activity: Activity, name: String): HandlerThread(name), Camera.PreviewCallback {
    private var mHandler: PreviewRendererHandler? = null
    private val mMainHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    private val mCameraEngine: CameraEngine by lazy { CameraEngine.instance }
    private val mCameraParams: CameraParams by lazy { CameraParams.instance }
    private val mFpsCounter: CameraFpsCounter by lazy { CameraFpsCounter() }

    private var mActivity: WeakReference<Activity>? = null
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mSurfaceTexture: SurfaceTexture? = null

    private var mPreviewBuffer: ByteArray? = null

    init {
        mActivity = WeakReference(activity)
    }

    fun bindHandler(handler: PreviewRendererHandler) {
        mHandler = handler
    }

    fun openCamera(surfaceTexture: SurfaceTexture) {
        mSurfaceTexture = surfaceTexture

        val activity = mActivity?.get() ?: return
        mCameraEngine.openCamera(activity, mCameraParams.mCameraId)
        mCameraEngine.startPreview(surfaceTexture)

        mPreviewBuffer = ByteArray(mCameraParams.mPreviewWidth * mCameraParams.mPreviewHeight * 3 / 2)
        mCameraEngine.setPreviewBuffer(this, mPreviewBuffer!!)
    }

    fun openCamera(holder: SurfaceHolder) {
        mSurfaceHolder = holder
        val activity = mActivity?.get() ?: return
        mCameraEngine.openCamera(activity, mCameraParams.mCameraId)
        mCameraEngine.startPreview(holder)

        mPreviewBuffer = ByteArray(mCameraParams.mPreviewWidth * mCameraParams.mPreviewHeight * 3 / 2)
        mCameraEngine.setPreviewBuffer(this, mPreviewBuffer!!)
    }

    /**
     * 关闭相机
     */
    private fun closeCamera() {
        mCameraEngine.releaseCamera()
    }

    /**
     * 释放相机
     */
    fun releaseCamera() {
        closeCamera()

        mSurfaceHolder = null

        mSurfaceTexture?.release()
        mSurfaceTexture = null

        mActivity?.clear()
        mActivity = null

        mFpsCounter.reset()

        mHandler?.removeCallbacksAndMessages(null)
        mHandler = null

        mMainHandler.removeCallbacksAndMessages(null)
    }

    /**
     * 切换摄像头
     */
    fun switchCamera() {
        closeCamera()
        mCameraParams.mCameraId = 1 - mCameraParams.mCameraId
        if (mSurfaceHolder != null) {
            openCamera(mSurfaceHolder!!)
        } else if (mSurfaceTexture != null) {
            openCamera(mSurfaceTexture!!)
        } else {
            mCameraParams.mCameraListener?.onCameraError(CameraErrno.ERRNO_CAMERA_SWITCH)
        }
    }

    /**
     * 聚焦，测光
     */
    fun focusAndMeteringOnArea(x: Int, y: Int) {
        val rect = Rect(x - 100, y - 100, x + 100, y + 100)

        val viewWidth = mCameraParams.mViewWidth
        val viewHeight = mCameraParams.mViewHeight

        val ratioLeft = rect.left.toFloat() / viewWidth
        val ratioTop = rect.top.toFloat() / viewHeight
        val ratioRight = rect.right.toFloat() / viewWidth
        val ratioBottom = rect.bottom.toFloat() / viewHeight

        rect.left = (ratioLeft * 2000 - 1000).toInt()
        rect.top = (ratioTop * 2000 - 1000).toInt()
        rect.right = (ratioRight * 2000 - 1000).toInt()
        rect.bottom = (ratioBottom * 2000 - 1000).toInt()

        rect.left = if (rect.left < -1000) -1000 else rect.left
        rect.top = if (rect.top < -1000) -1000 else rect.top
        rect.right = if (rect.right > 1000) 1000 else rect.right
        rect.bottom = if (rect.bottom > 1000) 1000 else rect.bottom

        if (checkAreaRect(rect)) {
            mCameraEngine.focusOnArea(rect)
            mCameraEngine.meteringLightOnArea(rect)
        }
    }

    private fun checkAreaRect(rect: Rect): Boolean {
        if (rect.left < -1000 || rect.left > 1000) {
            return false
        } else if (rect.top < -1000 || rect.top > 1000) {
            return false
        } else if (rect.right < -1000 || rect.right > 1000) {
            return false
        } else if (rect.bottom < -1000 || rect.bottom > 1000) {
            return false
        }
        return true
    }

    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        val context = mActivity?.get()
        if (context != null && data != null) {
//            FaceppHelper.instance.detect(context, data)
        }

        if (mPreviewBuffer != null) {
            camera?.addCallbackBuffer(mPreviewBuffer)
        }

        if (mCameraParams.isShowCameraFps) {
            mHandler?.sendMessage(mHandler?.obtainMessage(RendererMsgType.MSG_CALCULATE_FPS))
        }
    }

    fun calculateFps() {
        if (mCameraParams.mCameraFpsListener != null) {
            mFpsCounter.calculateFps()
            mMainHandler.post { mCameraParams.mCameraFpsListener!!.onFpsChaned(mFpsCounter.getFps()) }
        }
    }

    fun initFacepp() {
        val context = mActivity?.get() ?: return
        FaceppHelper.instance.init(context)
    }
}