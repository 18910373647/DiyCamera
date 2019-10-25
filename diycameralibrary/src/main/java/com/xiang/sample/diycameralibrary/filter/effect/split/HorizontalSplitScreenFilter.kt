package com.xiang.sample.diycameralibrary.filter.effect.split

import android.opengl.GLES20
import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import java.nio.FloatBuffer

class HorizontalSplitScreenFilter: BaseFilter() {
    private val mLeftFilter: HorizontalSplitFilter by lazy { HorizontalSplitFilter(true) }
    private val mRightFilter: HorizontalSplitFilter by lazy { HorizontalSplitFilter(false) }

    private var mLeftTextureHandle = 0
    private var mRightTextureHandle = 0

    private var mLeftTexture = 0
    private var mRightTexture = 0

    override fun onDrawFrameBuffer(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer): Int {
        val leftVertexFb = mLeftFilter.getLeftVertexFb()
        val leftTextureFb = mLeftFilter.getTextureFb()
        mLeftTexture = mLeftFilter.drawFrameBuffer(textureId, leftVertexFb, leftTextureFb)

        val rightVertexFb = mRightFilter.getRightVertexFb()
        val rightTextureFb = mRightFilter.getTextureFb()
        mRightTexture = mRightFilter.drawFrameBuffer(textureId, rightVertexFb, rightTextureFb)

        return super.onDrawFrameBuffer(textureId, vertexFb, fragmentFb)
    }

    override fun initShaderHandles() {
        super.initShaderHandles()

        mLeftTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_TEXTURE_BASE + 1)
        mRightTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_TEXTURE_BASE + 2)
    }

    override fun passShaderValue() {
        super.passShaderValue()

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(getTextureType(), mLeftTexture)
        GLES20.glUniform1i(mLeftTextureHandle, 1)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
        GLES20.glBindTexture(getTextureType(), mRightTexture)
        GLES20.glUniform1i(mRightTextureHandle, 2)
    }

    override fun getFragmentShader(): String {
        return "precision mediump float;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 0};\n" +
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 1};\n" +
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 2};\n" +

                "void main() {\n" +
                "   vec2 uv = $VARYING_TEXTURE;\n" +
                "   if (uv.x <= 0.5) {\n" +
                "       gl_FragColor = texture2D(${UNIFORM_TEXTURE_BASE + 1}, uv);\n" +
                "   } else {\n" +
                "       gl_FragColor = texture2D(${UNIFORM_TEXTURE_BASE + 2}, vec2(1.5 - uv.x, uv.y));\n" +
                "   }\n" +
                "}\n"
    }
}