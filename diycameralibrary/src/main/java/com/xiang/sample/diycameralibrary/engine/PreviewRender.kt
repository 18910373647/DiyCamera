package com.xiang.sample.diycameralibrary.engine

import android.app.Activity
import com.xiang.sample.diycameralibrary.utils.CameraParams
import java.lang.ref.WeakReference

abstract class PreviewRenderer {
    protected var mPreviewRendererThread: PreviewRendererThread? = null
    protected var mPreviewRendererHandler: PreviewRendererHandler? = null
    protected val mSyncObject = Any()
    protected var mActivity: WeakReference<Activity>? = null

    fun initRenderer(activity: Activity) {
        synchronized(mSyncObject) {
            mActivity = WeakReference(activity)
            mPreviewRendererThread = PreviewRendererThread(activity, "PreviewRendererThread")
            mPreviewRendererThread!!.start()

            mPreviewRendererHandler = PreviewRendererHandler(mPreviewRendererThread!!)
            mPreviewRendererThread!!.bindHandler(mPreviewRendererHandler!!)
        }
    }

    fun destroyRender() {
        synchronized(mSyncObject) {
            mPreviewRendererHandler?.removeCallbacksAndMessages(null)
            mPreviewRendererHandler = null

            mPreviewRendererThread?.quitSafely()
            try {
                mPreviewRendererThread?.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            mPreviewRendererThread = null
        }
    }

    fun switchCamera() {
        synchronized(mSyncObject) {
            mPreviewRendererHandler?.sendMessage(mPreviewRendererHandler?.obtainMessage(RendererMsgType.MSG_SWITCH_CAMERA))
        }
    }


    fun onSizeChanged(width: Int, height: Int) {
        CameraParams.instance.mViewWidth = width
        CameraParams.instance.mViewHeight = height
        mPreviewRendererHandler?.sendMessage(mPreviewRendererHandler?.obtainMessage(RendererMsgType.MSG_TEXTURE_VIEW_SIZE_CHANGED, width, height))
    }

    fun changeFocusOnArea(x: Int, y: Int) {
        mPreviewRendererHandler?.sendMessage(mPreviewRendererHandler?.obtainMessage(RendererMsgType.MSG_FOCUS_ON_AREA, x, y))
    }
}