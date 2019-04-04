package com.xiang.sample.diycameralibrary.filter.beauty

import android.opengl.GLES20
import com.xiang.sample.diycameralibrary.filter.base.BaseFilter

class HighPassFilter: BaseFilter() {
    var mBlurTexture = 0
    private var mBlurTextureHandle = 0

    override fun initShaderHandles() {
        super.initShaderHandles()

        mBlurTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_TEXTURE_BASE + 1)
    }

    override fun passShaderValue() {
        super.passShaderValue()

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBlurTexture)
        GLES20.glUniform1i(mBlurTextureHandle, 1)
    }

    override fun getFragmentShader(): String {
        return "precision mediump float;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 0};\n" + // 原图
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 1};\n" + // 经过高斯模糊后的图

                "void main() {\n" +
                "    vec4 color = texture2D(${UNIFORM_TEXTURE_BASE + 0}, $VARYING_TEXTURE);\n" +
                "    vec4 blur = texture2D(${UNIFORM_TEXTURE_BASE + 1}, $VARYING_TEXTURE);\n" +
                "    vec4 highPass = color - blur;\n" +

                // 强光处理 2 * color * color，将脸部斑点保留
                "    highPass.r = clamp(2.0 * highPass.r * highPass.r * 24.0, 0.0, 1.0);\n" +
                "    highPass.g = clamp(2.0 * highPass.g * highPass.g * 24.0, 0.0, 1.0);\n" +
                "    highPass.b = clamp(2.0 * highPass.b * highPass.b * 24.0, 0.0, 1.0);\n" +
                "    gl_FragColor = vec4(highPass.rgb, 1.0);\n" +
                "}\n"
    }
}
