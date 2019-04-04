package com.xiang.sample.diycameralibrary.utils

object FilterMethodHelper {

    fun colorLookup2DSquareLUT(): String {
        return "vec4 colorLookup2DSquareLUT(vec4 color, int dimension, float intensity, sampler2D lutTexture, float width, float height) {\n" +
                "   float x = sqrt(float(dimension));\n" +
                "   int row = int(floor(x + 0.5));\n" +
                "   float blueColor = color.b * float(dimension - 1);\n" +

                "   ivec2 quad1;\n" +
                "   quad1.y = int(floor(floor(blueColor) / float(row)));\n" +
                "   quad1.x = int(floor(blueColor) - float(quad1.y * row));\n" +

                "   ivec2 quad2;\n" +
                "   quad2.y = int(floor(ceil(blueColor) / float(row)));\n" +
                "   quad2.x = int(ceil(blueColor) - float(quad2.y * row));\n" +

                "   vec2 texPos1;\n" +
                "   texPos1.x = (float(quad1.x) * (1.0/float(row))) + 0.5/width + ((1.0/float(row) - 1.0/width) * color.r);\n" +
                "   texPos1.y = (float(quad1.y) * (1.0/float(row))) + 0.5/height + ((1.0/float(row) - 1.0/height) * color.g);\n" +

                "   vec2 texPos2;\n" +
                "   texPos2.x = (float(quad2.x) * (1.0/float(row))) + 0.5/width + ((1.0/float(row) - 1.0/width) * color.r);\n" +
                "   texPos2.y = (float(quad2.y) * (1.0/float(row))) + 0.5/height + ((1.0/float(row) - 1.0/height) * color.g);\n" +

                "   vec4 newColor1 = texture2D(lutTexture, texPos1);\n" +
                "   vec4 newColor2 = texture2D(lutTexture, texPos2);\n" +

                "   vec4 newColor = mix(newColor1, newColor2, float(fract(blueColor)));\n" +
                "   vec4 finalColor = mix(color, vec4(newColor.rgb, color.a), intensity);\n" +

                "   return finalColor;\n" +
                "}\n"
    }
}