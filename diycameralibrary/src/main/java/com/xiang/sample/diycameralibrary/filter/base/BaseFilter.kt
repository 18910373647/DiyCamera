package com.xiang.sample.diycameralibrary.filter.base

open class BaseFilter: AbsRenderingFrameBufferFilter() {
    override fun getVertexShader(): String {
        return "attribute vec4 $ATTRIBUTE_POSITION;\n" +
                "attribute vec2 $ATTRIBUTE_TEXTURE;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +

                "void main() {\n" +
                "    gl_Position = $ATTRIBUTE_POSITION;\n" +
                "    $VARYING_TEXTURE = $ATTRIBUTE_TEXTURE;\n" +
                "}\n"

    }

    override fun getFragmentShader(): String {
        return "precision mediump float;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +
                "uniform sampler2D $UNIFORM_INPUT_TEXTURE;\n" +

                "void main() {\n" +
                "    gl_FragColor = texture2D($UNIFORM_INPUT_TEXTURE, $VARYING_TEXTURE);\n" +
                "}\n"
    }

}