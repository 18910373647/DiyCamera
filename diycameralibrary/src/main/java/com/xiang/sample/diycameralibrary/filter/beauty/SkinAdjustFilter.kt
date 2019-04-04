package com.xiang.sample.diycameralibrary.filter.beauty

import android.opengl.GLES20
import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import com.xiang.sample.diycameralibrary.utils.SpecialEffectFilterParams

class SkinAdjustFilter: BaseFilter() {
    private val UNIFORM_INTENSITY = "intensity"
    private var mIntensityHandle = 0

    var mBlurTexture = 0
    private var mBlurTextureHandle = 0

    var mHighPassBlurTexture = 0
    private var mHighPassBlurTextureHandle = 0

    override fun initShaderHandles() {
        super.initShaderHandles()

        mIntensityHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_INTENSITY)
        mBlurTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_TEXTURE_BASE + 1)
        mHighPassBlurTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_TEXTURE_BASE + 2)
    }

    override fun passShaderValue() {
        super.passShaderValue()

        val intensity = SpecialEffectFilterParams.getBlurIntensity() * 0.8f
        GLES20.glUniform1f(mIntensityHandle, intensity)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBlurTexture)
        GLES20.glUniform1i(mBlurTextureHandle, 1)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mHighPassBlurTexture)
        GLES20.glUniform1i(mHighPassBlurTextureHandle, 2)
    }

    override fun getFragmentShader(): String {
        return "precision mediump float;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 0};\n" + // 原图
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 1};\n" + // 原图模糊图（低通滤波）
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 2};\n" + // 高通滤波后的模糊图
                "uniform float intensity;\n" +

                "void main() {\n" +
                "    vec4 color = texture2D(${UNIFORM_TEXTURE_BASE + 0}, $VARYING_TEXTURE);\n" +
                "    vec4 blur = texture2D(${UNIFORM_TEXTURE_BASE + 1}, $VARYING_TEXTURE);\n" +
                "    vec4 highPassBlur = texture2D(${UNIFORM_TEXTURE_BASE + 2}, $VARYING_TEXTURE);\n" +

                "    float value = clamp((min(color.b, blur.b) - 0.2) * 5.0, 0.0, 1.0);\n" +
                "    float maxChannelColor = max(max(highPassBlur.r, highPassBlur.g), highPassBlur.b);\n" +
                "    float curIntensity = (1.0 - maxChannelColor / (maxChannelColor + 0.2)) * value * intensity;\n" +
                "    vec3 result = mix(color.rgb, blur.rgb, curIntensity);\n" +
                "    gl_FragColor = vec4(result, 1.0);\n" +
                "}\n"
    }
}