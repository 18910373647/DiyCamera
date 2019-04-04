package com.xiang.sample.diycameralibrary.filter.gaussian

import android.opengl.GLES20
import com.xiang.sample.diycameralibrary.filter.base.BaseFilter

class GaussianBlurFilter: BaseFilter() {
    private val UNIFORM_TEXEL_OFFSET_HANDLE = "texelOffset"
    private val BLUR_OFFSET = 2

    private var mTexelOffsetHandle = 0
    private var mWidth = 0f
    private var mHeight = 0f
    private var mBlurSize = 0f

    fun setTexelOffset(width: Float, height: Float) {
        mWidth = width
        mHeight = height
    }

    fun setBlurSize(blurSize: Float) {
        mBlurSize = blurSize * BLUR_OFFSET
    }

    override fun initShaderHandles() {
        super.initShaderHandles()

        mTexelOffsetHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_TEXEL_OFFSET_HANDLE)
    }

    override fun passShaderValue() {
        super.passShaderValue()

        val texelWidthOffset = if (mWidth == 0f) 0f else mBlurSize / mWidth
        val texelHeightOffset = if (mHeight == 0f) 0f else mBlurSize / mHeight
        GLES20.glUniform2f(mTexelOffsetHandle, texelWidthOffset, texelHeightOffset)
    }

    override fun getVertexShader(): String {
        return "attribute vec4 $ATTRIBUTE_POSITION;\n" +
                "attribute vec2 $ATTRIBUTE_TEXTURE;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +
                "const int offset = 5;\n" +
                "uniform vec2 texelOffset;\n" +
                "varying vec4 blurOffsetCoordinate[offset];\n" +

                "void main() {\n" +
                "    gl_Position = $ATTRIBUTE_POSITION;\n" +
                "    $VARYING_TEXTURE = $ATTRIBUTE_TEXTURE;\n" +

                "    for (int i = 0; i < offset; i++) {\n" +
                "        blurOffsetCoordinate[i] = vec4(vec2($VARYING_TEXTURE.xy - float(i + 1) * texelOffset), vec2($VARYING_TEXTURE.xy + float(i + 1) * texelOffset));\n" +
                "    }\n" +
                "}\n"
    }

    override fun getFragmentShader(): String {
        return "precision mediump float;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +
                "uniform sampler2D $UNIFORM_INPUT_TEXTURE;\n" +
                "const int offset = 5;\n" +
                "varying vec4 blurOffsetCoordinate[offset];\n" +

                "void main() {\n" +
                "    vec4 currentColor = texture2D($UNIFORM_INPUT_TEXTURE, $VARYING_TEXTURE);\n" + //

                "    for (int i = 0; i < offset; i++) {\n" +
                "        currentColor.rgb += texture2D($UNIFORM_INPUT_TEXTURE, blurOffsetCoordinate[i].xy).rgb;\n" +
                "        currentColor.rgb += texture2D($UNIFORM_INPUT_TEXTURE, blurOffsetCoordinate[i].zw).rgb;\n" +
                "    }\n" +

                "    vec3 blur = currentColor.rgb / float(2 * 5 + 1);\n" +
                "    gl_FragColor = vec4(blur, currentColor.a);\n" +
//                "    gl_FragColor = currentColor;\n" +
                "}\n"
    }
}