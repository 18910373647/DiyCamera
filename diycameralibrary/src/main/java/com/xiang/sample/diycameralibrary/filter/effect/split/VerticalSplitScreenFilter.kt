package com.xiang.sample.diycameralibrary.filter.effect.split

import android.opengl.GLES20
import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import java.nio.FloatBuffer

class VerticalSplitScreenFilter: BaseFilter() {
    private val mTopSplitFilter: VerticalSplitFilter by lazy { VerticalSplitFilter(true) }
    private val mBottomSplitFilter: VerticalSplitFilter by lazy { VerticalSplitFilter(false) }

    private var mTopTextureHandle = 0
    private var mBottomTextureHandle = 0
    private var mTopTexture = 0
    private var mBottomTexture = 0

    override fun onDrawFrameBuffer(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer): Int {
        val topVertexFb = mTopSplitFilter.getTopVertexBuffer()
        val topFragmentFb = mTopSplitFilter.getTextureCoordinateBuffer()
        mTopTexture = mTopSplitFilter.drawFrameBuffer(textureId, topVertexFb, topFragmentFb)

        val bottomVertexFb = mBottomSplitFilter.getBottomVertexBuffer()
        val bottomFragmentFb = mBottomSplitFilter.getTextureCoordinateBuffer()
        mBottomTexture = mBottomSplitFilter.drawFrameBuffer(textureId, bottomVertexFb, bottomFragmentFb)

        return super.onDrawFrameBuffer(textureId, vertexFb, fragmentFb)
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        mTopTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_TEXTURE_BASE + 1)
        mBottomTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_TEXTURE_BASE + 2)
    }

    override fun passShaderValue() {
        super.passShaderValue()

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(getTextureType(), mTopTexture)
        GLES20.glUniform1i(mTopTextureHandle, 1)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
        GLES20.glBindTexture(getTextureType(), mBottomTexture)
        GLES20.glUniform1i(mBottomTextureHandle, 2)
    }

    override fun getFragmentShader(): String {
        return "precision mediump float;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 0};\n" +
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 1};\n" + // 上
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 2};\n" + // 下

                "void main() {\n" +
                "   vec2 uv = $VARYING_TEXTURE;\n" +
                "   if (uv.y <= 0.5) {\n" +
                "       gl_FragColor = texture2D(${UNIFORM_TEXTURE_BASE + 2}, vec2(uv.x, 0.5 - uv.y));\n" +
                "   } else {\n" +
                "       gl_FragColor = texture2D(${UNIFORM_TEXTURE_BASE + 1}, uv);\n" +
                "   }\n" +
//                "   gl_FragColor = texture2D(${UNIFORM_TEXTURE_BASE + 0}, uv);\n" +
                "}\n"
    }
}