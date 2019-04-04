package com.xiang.sample.diycameralibrary.engine

import android.graphics.SurfaceTexture
import android.view.TextureView
import java.lang.ref.WeakReference

class TextureViewPreviewRenderer private constructor(): PreviewRenderer(), TextureView.SurfaceTextureListener {
    private var mTextureView: WeakReference<TextureView>? = null

    companion object {
        val instance: TextureViewPreviewRenderer by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            TextureViewPreviewRenderer()
        }
    }

    fun setTextureView(textureView: TextureView) {
        mTextureView?.clear()
        mTextureView = WeakReference(textureView)
        mTextureView?.get()?.surfaceTextureListener = this
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        onSizeChanged(width, height)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        mPreviewRendererHandler?.sendMessage(mPreviewRendererHandler?.obtainMessage(RendererMsgType.MSG_TEXTURE_VIEW_DESTROY))
        return false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        onSizeChanged(width, height)
        mPreviewRendererHandler?.sendMessage(mPreviewRendererHandler?.obtainMessage(RendererMsgType.MSG_TEXTURE_VIEW_CREATE, surface))
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {

    }
}