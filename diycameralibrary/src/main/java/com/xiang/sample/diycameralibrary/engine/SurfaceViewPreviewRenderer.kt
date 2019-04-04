package com.xiang.sample.diycameralibrary.engine

import android.view.SurfaceHolder
import android.view.SurfaceView
import java.lang.ref.WeakReference

class SurfaceViewPreviewRenderer private constructor(): PreviewRenderer(), SurfaceHolder.Callback {
    private var mSurfaceView: WeakReference<SurfaceView>? = null

    companion object {
        val instance: SurfaceViewPreviewRenderer by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SurfaceViewPreviewRenderer()
        }
    }

    fun setSurfaceView(surfaceView: SurfaceView) {
        mSurfaceView?.clear()
        mSurfaceView = WeakReference(surfaceView)
        mSurfaceView?.get()?.holder?.addCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        onSizeChanged(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mPreviewRendererHandler?.sendMessage(mPreviewRendererHandler?.obtainMessage(RendererMsgType.MSG_SURFACE_VIEW_DESTROY))
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mPreviewRendererHandler?.sendMessage(mPreviewRendererHandler?.obtainMessage(RendererMsgType.MSG_SURFACE_VIEW_CREATE, holder))
    }
}