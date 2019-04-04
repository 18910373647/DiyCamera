package com.xiang.sample.diycameralibrary.filter.lookup

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import com.xiang.sample.diycameralibrary.utils.FilterMethodHelper
import com.xiang.sample.diycameralibrary.utils.LookupFilterParams
import com.xiang.sample.diycameralibrary.utils.OpenGLUtils

class DiyLookupFilter(context: Context?): BaseFilter() {
    private var mCurrentLookupTextureHandle = 0
    private var mCurrentLookupTexture = 0
    private var mNextLookupTextureHandle = 0
    private var mNextLookupTexture = 0

    private var mOffsetHandle = 0
    private var isLeftHandle = 0
    private var mIntensityHandle = 0
    private var mLastFilterIndex = -1
    private var mLastNextFilterIndex = -1
    private var mContext: Context? = null

    init {
        mContext = context
    }

    override fun getFragmentShader(): String {
        return "precision mediump float;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +
                "uniform sampler2D $UNIFORM_INPUT_TEXTURE;\n" +
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 1};\n" +
                "uniform sampler2D ${UNIFORM_TEXTURE_BASE + 2};\n" +
                "uniform int origin;\n" +
                "uniform float offset;\n" +
                "uniform int isLeft;\n" +
                "uniform float intensity;\n" +
                "\n" +
                FilterMethodHelper.colorLookup2DSquareLUT() +
                "\n" +
                "void main() {\n" +
                "    vec4 color = texture2D($UNIFORM_INPUT_TEXTURE, $VARYING_TEXTURE);\n" +
                "    vec4 current = colorLookup2DSquareLUT(color, 64, intensity, ${UNIFORM_TEXTURE_BASE + 1}, 512.0, 512.0);\n" +
                "    vec4 next = colorLookup2DSquareLUT(color, 64, intensity, ${UNIFORM_TEXTURE_BASE + 2}, 512.0, 512.0);\n" +

                "    if (offset <= 0.05) {\n" +
                "        gl_FragColor = current;\n" +
                "        return;\n" +
                "    } " +

                "    bool left = isLeft == 1;\n" +
                "    if ($VARYING_TEXTURE.x <= offset) {\n" +
                "        gl_FragColor = left ? current : next;\n" +
                "    } else {\n" +
                "        gl_FragColor = left ? next : current;\n" +
                "    }\n" +
                "}\n"
    }

    override fun initShaderHandles() {
        super.initShaderHandles()

        mCurrentLookupTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_TEXTURE_BASE + 1)
        mNextLookupTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_TEXTURE_BASE + 2)
        mOffsetHandle = GLES20.glGetUniformLocation(mProgramHandle, "offset")
        isLeftHandle = GLES20.glGetUniformLocation(mProgramHandle, "isLeft")
        mIntensityHandle = GLES20.glGetUniformLocation(mProgramHandle, "intensity")
    }

    override fun passShaderValue() {
        super.passShaderValue()

        val isLeft = LookupFilterParams.isLookupFilterLeftScroll()
        val offset = LookupFilterParams.getLookupFilterScrollOffset()
        val state = LookupFilterParams.getLookupFilterScrollState()
        val currentFilterIndex = LookupFilterParams.getLookupFilterIndex()

        if (state != 0) {
            val nextFilterIndex = if (isLeft) currentFilterIndex + 1 else currentFilterIndex - 1
            if (mLastNextFilterIndex != nextFilterIndex) {
                mLastNextFilterIndex = nextFilterIndex
                mNextLookupTexture = updateFilterTexture(nextFilterIndex, mNextLookupTexture)
            }
        }


        if (mLastFilterIndex != currentFilterIndex) {
            mLastFilterIndex = currentFilterIndex
            mCurrentLookupTexture = updateFilterTexture(currentFilterIndex, mCurrentLookupTexture)
        }

        GLES20.glUniform1f(mOffsetHandle, 1f - offset)
        GLES20.glUniform1i(isLeftHandle, if (isLeft) 1 else 0)
        GLES20.glUniform1f(mIntensityHandle, LookupFilterParams.getLookupFilterIntensity())

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mCurrentLookupTexture)
        GLES20.glUniform1i(mCurrentLookupTextureHandle, 1)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mNextLookupTexture)
        GLES20.glUniform1i(mNextLookupTextureHandle, 2)
    }

    private fun updateFilterTexture(index: Int, texture: Int): Int {
        val lookupFilterCount = LookupFilterParams.getLookupFilterCount()
        val position = when {
            index < 0 -> 0
            index >= lookupFilterCount -> lookupFilterCount - 1
            else -> index
        }

        var current = texture
        val inputStream = mContext!!.assets.open("lookups/$position/lookup.png")
        val bitmap = BitmapFactory.decodeStream(inputStream)
        if (current == 0) {
            current = OpenGLUtils.ImageToTexture(bitmap)
        } else {
            OpenGLUtils.updateImageToTexture(current, bitmap)
        }
        bitmap.recycle()
        return current
    }

    override fun release() {
        super.release()

        if (mCurrentLookupTexture != 0) {
            GLES20.glDeleteTextures(1, intArrayOf(mCurrentLookupTexture), 0)
            mCurrentLookupTexture = 0
        }

        if (mNextLookupTexture != 0) {
            GLES20.glDeleteTextures(1, intArrayOf(mNextLookupTexture), 0)
            mNextLookupTexture = 0
        }
    }
}