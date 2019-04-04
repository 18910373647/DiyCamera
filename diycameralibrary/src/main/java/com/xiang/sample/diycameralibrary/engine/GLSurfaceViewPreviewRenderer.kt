package com.xiang.sample.diycameralibrary.engine

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.xiang.sample.diycameralibrary.utils.OpenGLUtils
import java.lang.ref.WeakReference
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLSurfaceViewPreviewRenderer: PreviewRenderer(), GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private var mGLSurfaceView: WeakReference<GLSurfaceView>? = null
    private var mTextureId: Int = 0
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mMatrix = FloatArray(16)

    companion object {
        val instance: GLSurfaceViewPreviewRenderer by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            GLSurfaceViewPreviewRenderer()
        }
    }

    fun setGLSurfaceView(glSurfaceView: GLSurfaceView) {
        mGLSurfaceView?.clear()
        mGLSurfaceView = WeakReference(glSurfaceView)
        mGLSurfaceView?.get()?.setEGLContextClientVersion(2)
        mGLSurfaceView?.get()?.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        mGLSurfaceView?.get()?.setRenderer(this)
        mGLSurfaceView?.get()?.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
//        mGLSurfaceView?.get()?.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        mSurfaceTexture?.updateTexImage()
        mSurfaceTexture?.getTransformMatrix(mMatrix)
        RendererManager.instance.renderer(mTextureId, mMatrix)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        onSizeChanged(width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mTextureId = OpenGLUtils.createOESTexture()
        mSurfaceTexture = SurfaceTexture(mTextureId)
        mSurfaceTexture?.setOnFrameAvailableListener(this)
        mPreviewRendererHandler?.sendMessage(mPreviewRendererHandler?.obtainMessage(RendererMsgType.MSG_GL_SURFACE_VIEW_CREATE, mSurfaceTexture))
        mPreviewRendererHandler?.sendMessage(mPreviewRendererHandler?.obtainMessage(RendererMsgType.MSG_INIT_RENDER_MANAGER, mActivity?.get()))
        mPreviewRendererHandler?.sendMessage(mPreviewRendererHandler?.obtainMessage(RendererMsgType.MSG_INIT_FACEPP))
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        if (mGLSurfaceView?.get()?.renderMode == GLSurfaceView.RENDERMODE_WHEN_DIRTY) {
            mGLSurfaceView?.get()?.requestRender()
        }
    }
}