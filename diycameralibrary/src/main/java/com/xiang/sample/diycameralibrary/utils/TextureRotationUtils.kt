package com.xiang.sample.diycameralibrary.utils

object TextureRotationUtils {
    val VERTEX_COORDS = floatArrayOf(
        -1f, -1f,
         1f, -1f,
        -1f,  1f,
         1f,  1f)

    val TEXTURE_COORDS = floatArrayOf(
        0f, 0f,
        1f, 0f,
        0f, 1f,
        1f, 1f
    )

    val TEXTURE_COORDS_90 = floatArrayOf(
        1f, 0f,
        1f, 1f,
        0f, 0f,
        0f, 1f
    )

    val TEXTURE_COORDS_180 = floatArrayOf(
        1f, 1f,
        0f, 1f,
        1f, 0f,
        0f, 0f
    )

    val TEXTURE_COORDS_270 = floatArrayOf(
        0f, 1f,
        0f, 0f,
        1f, 1f,
        1f, 0f
    )


}