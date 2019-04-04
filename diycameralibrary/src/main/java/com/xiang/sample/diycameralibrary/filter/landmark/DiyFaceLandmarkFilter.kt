package com.xiang.sample.diycameralibrary.filter.landmark

import android.opengl.GLES20
import android.util.Log
import com.xiang.sample.diycameralibrary.engine.FaceppHelper
import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class DiyFaceLandmarkFilter: BaseFilter() {
    private var mPointVertexBuffer: FloatBuffer? = null

    override fun initShaderHandles() {
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, ATTRIBUTE_POSITION)
    }

    override fun drawTexture(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer) {
        if (!FaceppHelper.instance.hasFace) {
            return
        }

        val landmark = FaceppHelper.instance.mLandmark
        if (landmark.isEmpty()) {
            return
        }

        if (mPointVertexBuffer == null) {
            mPointVertexBuffer = ByteBuffer.allocateDirect(landmark.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        }

        mPointVertexBuffer!!.clear()
        mPointVertexBuffer!!.put(landmark, 0, landmark.size)
        mPointVertexBuffer!!.position(0)

        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 8, mPointVertexBuffer)
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, landmark.size / 2)
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    override fun getVertexShader(): String {
        return "attribute vec4 $ATTRIBUTE_POSITION;\n" +
                "void main() {\n" +
                "    gl_Position = $ATTRIBUTE_POSITION;\n" +
                "    gl_PointSize = 10.0;\n" +
                "}\n"
    }

    override fun getFragmentShader(): String {
        return "precision mediump float;\n" +
                "void main() {\n" +
                "    gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);\n" +
                "}\n"
    }
}