package com.xiang.sample.diycameralibrary.filter.effect.emboss

import android.opengl.GLES10
import android.opengl.GLES20
import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import com.xiang.sample.diycameralibrary.utils.CameraParams

class EmbossFilter: BaseFilter() {
    private val UNIFORM_SIZE = "size"
    private var mSizeHandle = 0

    override fun initShaderHandles() {
        super.initShaderHandles()

        mSizeHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_SIZE)
    }

    override fun passShaderValue() {
        super.passShaderValue()

        GLES20.glUniform2f(mSizeHandle, CameraParams.instance.mPreviewWidth.toFloat(), CameraParams.instance.mPreviewHeight.toFloat())
    }

    override fun getFragmentShader(): String {
        return "precision highp float;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +
                "uniform sampler2D $UNIFORM_INPUT_TEXTURE;\n" +
                "uniform vec2 size;\n" +
                "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
                "const highp vec3 W1 = vec3(0.3, 0.59, 0.11);\n" +
                "const vec4 bkColor = vec4(0.5, 0.5, 0.5, 1.0);\n" +

                "void main() {\n" +
//                "    vec2 tex = $VARYING_TEXTURE;\n" +
//                "    vec2 upLeftUV = vec2(tex.x - 1.0 / size.x, tex.y - 1.0 / size.y);\n" +
                "    vec4 curColor = texture2D($UNIFORM_INPUT_TEXTURE, $VARYING_TEXTURE);\n" +
//                "    vec4 upLeftColor = texture2D($UNIFORM_INPUT_TEXTURE, upLeftUV);\n" +
//                "    vec4 delColor = curColor - upLeftColor;\n" +
//                "    float luminance = dot(delColor.rgb, W);\n" +
//                "    gl_FragColor = vec4(vec3(luminance), 0.0);\n" +
//                "    gl_FragColor = vec4(vec3(luminance), 0.0) + bkColor;" +
                "   float gray = curColor.r * W1.x + curColor.y * W1.y + curColor.b * W1.z;\n" +
//                "   gl_FragColor = vec4(gray, gray, gray, curColor.a);\n" +
                "   gl_FragColor = vec4(gray, gray, gray, curColor.a);\n" +
                "}\n"
    }
}