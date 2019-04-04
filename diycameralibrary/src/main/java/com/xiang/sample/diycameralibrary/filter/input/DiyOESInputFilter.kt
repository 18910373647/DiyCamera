package com.xiang.sample.diycameralibrary.filter.input

import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.xiang.sample.diycameralibrary.filter.base.BaseFilter

class DiyOESInputFilter: BaseFilter() {
    private var mMatrixHandle = 0
    private var mMatrix: FloatArray? = null

    override fun getVertexShader(): String {
        return "attribute vec4 $ATTRIBUTE_POSITION;\n" +
                "attribute vec4 $ATTRIBUTE_TEXTURE;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +
                "uniform mat4 matrix;\n" +

                "void main() {\n" +
                "    gl_Position = $ATTRIBUTE_POSITION;\n" +
                "    $VARYING_TEXTURE = (matrix * $ATTRIBUTE_TEXTURE).xy;\n" +
                "}\n"
    }

    override fun getFragmentShader(): String {
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +
                "uniform samplerExternalOES $UNIFORM_INPUT_TEXTURE;\n" +

                "void main() {\n" +
                "    gl_FragColor = texture2D($UNIFORM_INPUT_TEXTURE, $VARYING_TEXTURE);\n" +
                "}\n"
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        mMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "matrix")
    }

    override fun passShaderValue() {
        super.passShaderValue()
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMatrix!!, 0)
    }

    override fun getTextureType(): Int {
        return GLES11Ext.GL_TEXTURE_EXTERNAL_OES
    }

    fun setMatrix(matrix: FloatArray) {
        mMatrix = matrix
    }
}