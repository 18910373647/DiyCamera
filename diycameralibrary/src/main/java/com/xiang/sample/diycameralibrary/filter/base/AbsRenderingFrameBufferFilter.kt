package com.xiang.sample.diycameralibrary.filter.base

import android.opengl.GLES20
import com.xiang.sample.diycameralibrary.utils.CameraParams
import com.xiang.sample.diycameralibrary.utils.OpenGLUtils
import java.nio.FloatBuffer

abstract class AbsRenderingFrameBufferFilter: AbsRenderingTextureFilter() {
    private var mFrameBuffers: IntArray? = null
    private var mFrameBufferTextures: IntArray? = null

    private var mFrameWidth = -1
    private var mFrameHeight = -1

    private fun initFrameBuffer(width: Int, height: Int) {
        if (width == 0 || height == 0) {
            return
        }

        if (mFrameBuffers != null && (mFrameWidth != width || mFrameHeight != height)) {
            destroyFrameBuffer()
        }

        if (mFrameBuffers == null) {
            mFrameWidth = width
            mFrameHeight = height
            mFrameBuffers = IntArray(1)
            mFrameBufferTextures = IntArray(1)
            OpenGLUtils.createFrameBuffer(mFrameBuffers!!, mFrameBufferTextures!!, width, height)
        }
    }

    private fun destroyFrameBuffer() {
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(1, mFrameBufferTextures, 0)
            mFrameBufferTextures = null
        }

        if (mFrameBuffers != null) {
            GLES20.glDeleteBuffers(1, mFrameBuffers, 0)
            mFrameBuffers = null
        }

        mFrameWidth = -1
        mFrameHeight = -1
    }

    fun drawFrameBuffer(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer): Int {
        if (!isInitGLContext) {
            initGLContext()
        }

        return onDrawFrameBuffer(textureId, vertexFb, fragmentFb)
    }

    protected open fun onDrawFrameBuffer(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer): Int {
        GLES20.glViewport(0, 0, CameraParams.instance.mPreviewWidth, CameraParams.instance.mPreviewHeight)
        initFrameBuffer(CameraParams.instance.mPreviewWidth, CameraParams.instance.mPreviewHeight)
        if (mFrameBuffers == null || mFrameBufferTextures == null) {
            return textureId
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers!![0])
        GLES20.glUseProgram(mProgramHandle)
        drawTexture(textureId, vertexFb, fragmentFb)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        return mFrameBufferTextures!![0]
    }

    override fun release() {
        super.release()
        destroyFrameBuffer()
    }
}