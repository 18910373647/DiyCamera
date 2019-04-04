package com.xiang.sample.diycameralibrary.engine

import android.app.Activity
import android.opengl.GLSurfaceView
import android.support.annotation.IntDef
import android.view.SurfaceView
import android.view.TextureView
import com.xiang.sample.diycameralibrary.interfaces.ICameraFpsListener
import com.xiang.sample.diycameralibrary.interfaces.ICameraListener
import com.xiang.sample.diycameralibrary.utils.CameraParams

class PreviewManager {

    private val mCameraParams: CameraParams by lazy { CameraParams.instance }
    private var mPreviewRenderer: PreviewRenderer? = null

    fun setInputType(@RendererInputType.InputType type: Int): PreviewManager {
        mPreviewRenderer = when (type) {
            RendererInputType.INPUT_SURFACE_VIEW -> SurfaceViewPreviewRenderer.instance
            RendererInputType.INPUT_TEXTURE_VIEW -> TextureViewPreviewRenderer.instance
            RendererInputType.INPUT_GL_SURFACE_VIEW -> GLSurfaceViewPreviewRenderer.instance
            else -> null
        }
        return this
    }

    fun setCameraListener(listener: ICameraListener): PreviewManager {
        mCameraParams.mCameraListener = listener
        return this
    }

    fun setCameraFpsListener(listener: ICameraFpsListener): PreviewManager {
        mCameraParams.mCameraFpsListener = listener
        return this
    }

    fun showFps(show: Boolean): PreviewManager {
        mCameraParams.isShowCameraFps = show
        return this
    }

    fun setSurfaceView(surfaceView: SurfaceView): PreviewManager {
        if (mPreviewRenderer is SurfaceViewPreviewRenderer) {
            (mPreviewRenderer as SurfaceViewPreviewRenderer).setSurfaceView(surfaceView)
        }
        return this
    }

    fun setTextureView(textureView: TextureView): PreviewManager {
        if (mPreviewRenderer is TextureViewPreviewRenderer) {
            (mPreviewRenderer as TextureViewPreviewRenderer).setTextureView(textureView)
        }
        return this
    }

    fun setGLSurfaceView(glSurfaceView: GLSurfaceView): PreviewManager {
        if (mPreviewRenderer is GLSurfaceViewPreviewRenderer) {
            (mPreviewRenderer as GLSurfaceViewPreviewRenderer).setGLSurfaceView(glSurfaceView)
        }
        return this
    }

    fun changeFocusOnArea(x: Float, y: Float) {
        mPreviewRenderer?.changeFocusOnArea(x.toInt(), y.toInt())
    }

    fun switchCamera() {
        mPreviewRenderer?.switchCamera()
    }

    fun start(activity: Activity) {
        mPreviewRenderer?.initRenderer(activity)
    }

    fun destroy() {
        mPreviewRenderer?.destroyRender()
    }
}

object RendererInputType {
    const val INPUT_SURFACE_VIEW = 0
    const val INPUT_TEXTURE_VIEW = 1
    const val INPUT_GL_SURFACE_VIEW = 2

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(INPUT_SURFACE_VIEW, INPUT_TEXTURE_VIEW, INPUT_GL_SURFACE_VIEW)
    annotation class InputType
}