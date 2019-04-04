package com.xiang.sample.diycameralibrary.filter.base

import android.opengl.GLES20
import com.xiang.sample.diycameralibrary.utils.CameraParams
import com.xiang.sample.diycameralibrary.utils.OpenGLUtils
import java.nio.FloatBuffer

abstract class AbsRenderingTextureFilter {
    protected val ATTRIBUTE_POSITION = "position"
    protected val ATTRIBUTE_TEXTURE= "inputTextureCoordinate"
    protected val VARYING_TEXTURE = "textureCoordinate"
    protected val UNIFORM_TEXTURE_BASE = "inputTexture"
    protected val UNIFORM_INPUT_TEXTURE = UNIFORM_TEXTURE_BASE + 0

    protected var mPositionHandle = 0
    protected var mTexCoordHandle = 0
    protected var mTextureHandle = 0
    protected var mProgramHandle = 0

    protected var isInitGLContext = false

    protected fun initGLContext() {
        val vertexId = OpenGLUtils.createShader(GLES20.GL_VERTEX_SHADER, getVertexShader())
        val fragmentId = OpenGLUtils.createShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShader())
        mProgramHandle = OpenGLUtils.createProgram(vertexId, fragmentId)
        initShaderHandles()
        isInitGLContext = true
    }

    protected open fun initShaderHandles() {
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, ATTRIBUTE_POSITION)
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgramHandle, ATTRIBUTE_TEXTURE)
        mTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_INPUT_TEXTURE)
    }

    fun drawFrame(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer) {
        if (!isInitGLContext) {
            initGLContext()
        }

        GLES20.glViewport(0, 0, CameraParams.instance.mViewWidth, CameraParams.instance.mViewHeight)
        GLES20.glUseProgram(mProgramHandle)
        onDrawFrame(textureId, vertexFb, fragmentFb)
    }

    protected open fun onDrawFrame(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer) {
        drawTexture(textureId, vertexFb, fragmentFb)
    }

    protected open fun drawTexture(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer) {
        passShaderValue()

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(getTextureType(), textureId)
        GLES20.glUniform1i(mTextureHandle, 0)

        vertexFb.position(0)
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexFb)
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        fragmentFb.position(0)
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, fragmentFb)
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        disableDrawArray()
    }

    protected open fun passShaderValue() {

    }

    protected open fun disableDrawArray() {
        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mTexCoordHandle)
        GLES20.glBindTexture(getTextureType(), 0)
    }

    open fun release() {
        if (isInitGLContext) {
            GLES20.glDeleteProgram(mProgramHandle)
        }
    }

    protected open fun getTextureType(): Int {
        return GLES20.GL_TEXTURE_2D
    }

    abstract fun getVertexShader(): String

    abstract fun getFragmentShader(): String
}