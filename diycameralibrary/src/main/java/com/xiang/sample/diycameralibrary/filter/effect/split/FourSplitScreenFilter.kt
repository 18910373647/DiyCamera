package com.xiang.sample.diycameralibrary.filter.effect.split

import android.opengl.GLES20
import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import java.nio.FloatBuffer

class FourSplitScreenFilter: BaseFilter() {
    private val mTopLeftFilter: FourSplitFilter by lazy { FourSplitFilter() }
    private val mTopRightFilter: FourSplitFilter by lazy { FourSplitFilter() }
    private val mBottomLeftFilter: FourSplitFilter by lazy { FourSplitFilter() }
    private val mBottomRightFilter: FourSplitFilter by lazy { FourSplitFilter() }

    private var mTopLeftTextureHandle = 0
    private var mTopRightTextureHandle = 0
    private var mBottomLeftTextureHandle = 0
    private var mBottomRightTextureHandle = 0

    private var mTopLeftTexture = 0
    private var mTopRightTexture = 0
    private var mBottomLeftTexture = 0
    private var mBottomRightTexture = 0

    override fun onDrawFrameBuffer(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer): Int {
        val topLeftVertexFb = mTopLeftFilter.getTopLeftVertexFb()
        val topLeftTextureFb = mTopLeftFilter.getTextureFb()
        mTopLeftTexture = mTopLeftFilter.drawFrameBuffer(textureId, topLeftVertexFb, topLeftTextureFb)

        val topRightVertexFb = mTopRightFilter.getTopRightVertexFb()
        val topRightTextureFb = mTopRightFilter.getTextureFb()
        mTopRightTexture = mTopRightFilter.drawFrameBuffer(textureId, topRightVertexFb, topRightTextureFb)

        val bottomLeftVertexFb = mBottomLeftFilter.getBottomLeftVertexFb()
        val bottomLeftTextureFb = mBottomLeftFilter.getTextureFb()
        mBottomLeftTexture = mBottomLeftFilter.drawFrameBuffer(textureId, bottomLeftVertexFb, bottomLeftTextureFb)

        val bottomRightVertexFb = mBottomRightFilter.getBottomRightVertexFb()
        val bottomRightTextureFb = mBottomRightFilter.getTextureFb()
        mBottomRightTexture = mBottomRightFilter.drawFrameBuffer(textureId, bottomRightVertexFb, bottomRightTextureFb)

        return super.onDrawFrameBuffer(textureId, vertexFb, fragmentFb)
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        mTopLeftTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_TEXTURE_BASE + 1)
        mTopRightTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_TEXTURE_BASE + 2)
        mBottomLeftTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_TEXTURE_BASE + 3)
        mBottomRightTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_TEXTURE_BASE + 4)
    }

    override fun passShaderValue() {
        super.passShaderValue()

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(getTextureType(), mTopLeftTexture)
        GLES20.glUniform1i(mTopLeftTextureHandle, 1)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
        GLES20.glBindTexture(getTextureType(), mTopRightTexture)
        GLES20.glUniform1i(mTopRightTextureHandle, 2)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
        GLES20.glBindTexture(getTextureType(), mBottomLeftTexture)
        GLES20.glUniform1i(mBottomLeftTextureHandle, 3)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE4)
        GLES20.glBindTexture(getTextureType(), mBottomRightTexture)
        GLES20.glUniform1i(mBottomRightTextureHandle, 4)
    }

    override fun getFragmentShader(): String {
        return "precision mediump float;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 0};\n" +
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 1};\n" +
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 2};\n" +
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 3};\n" +
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 4};\n" +

                "void main() {\n" +
                "   vec2 uv = $VARYING_TEXTURE;\n" +
                "   if (uv.x <= 0.5 && uv.y >= 0.5) {\n" + // top left
                "       gl_FragColor = texture2D(${UNIFORM_TEXTURE_BASE + 1}, uv);\n" +
                "   } else if (uv.x > 0.5 && uv.y >= 0.5) {\n" + // top right
                "       gl_FragColor = texture2D(${UNIFORM_TEXTURE_BASE + 2}, vec2(1.5 - uv.x, uv.y));\n" +
                "   } else if (uv.x <= 0.5 && uv.y < 0.5) {\n" + // bottom left
                "       gl_FragColor = texture2D(${UNIFORM_TEXTURE_BASE + 3}, vec2(uv.x, 0.5 - uv.y));\n" +
                "   } else if (uv.x > 0.5 && uv.y < 0.5) {\n" + // bottom right
                "       gl_FragColor = texture2D(${UNIFORM_TEXTURE_BASE + 4}, vec2(1.5 - uv.x, 0.5 - uv.y));\n" +
                "   }\n" +
                "}\n"
    }
}