package com.xiang.sample.diycameralibrary.filter.effect.edgeblur

import android.opengl.GLES20
import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import java.nio.FloatBuffer

class EdgeBlurFilter: BaseFilter() {
    private val UNIFORM_OFFSET = "offset"
    private var mOffsetHandle = 0

    var mBlurTexture = 0
    private var mBlurTextureHandle = 0

    override fun initShaderHandles() {
        super.initShaderHandles()

        mBlurTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_TEXTURE_BASE + 1)
        mOffsetHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_OFFSET)
    }

    override fun passShaderValue() {
        super.passShaderValue()

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBlurTexture)
        GLES20.glUniform1i(mBlurTextureHandle, 1)

        GLES20.glUniform1f(mOffsetHandle, 0.15f)
    }

    override fun getFragmentShader(): String {
        return "precision mediump float;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 0};\n" + // 原图
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 1};\n" + // 模糊后的图
                "uniform float offset;\n" +

                "void main() {\n" +
                "    vec2 uv = $VARYING_TEXTURE;\n" +
                "    vec4 color;\n" +
                "    if (uv.x >= offset && uv.x <= 1.0 - offset && uv.y >= offset && uv.y <= 1.0 - offset) {\n" +
                "        float scale  = 1.0 / (1.0 - 2.0 * offset);\n" +
                "        vec2 newUV = vec2((uv.x - offset) * scale, (uv.y - offset) * scale);\n" +
                "        color = texture2D(${UNIFORM_TEXTURE_BASE + 0}, newUV);\n" +
                "    } else {\n" +
                "        color = texture2D(${UNIFORM_TEXTURE_BASE + 1}, uv);\n" +
                "    }\n" +
                "    gl_FragColor = color;\n" +
                "}\n"
    }
}