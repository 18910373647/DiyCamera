package com.xiang.sample.diycameralibrary.filter.watermark

import android.graphics.*
import android.opengl.GLES20
import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import com.xiang.sample.diycameralibrary.utils.CameraParams
import com.xiang.sample.diycameralibrary.utils.OpenGLUtils
import com.xiang.sample.globallibrary.DiyCameraKit
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class DiyWaterMarkFilter: BaseFilter() {
    private val UNIFORM_TYPE = "type"
    private var mTypeHandle = 0

    private var mWaterMarkBitmap: Bitmap? = null
    private var mWaterMarkTexture = 0

    private val mBitmapWidth: Float by lazy { DiyCameraKit.getPixels(80f).toFloat() }
    private val mBitmapHeight: Float by lazy { DiyCameraKit.getPixels(50f).toFloat() }

    private var mVertexFb: FloatBuffer? = null
    private var mTextureFb: FloatBuffer? = null

    init {
        // TODO 放入线程池中
        mWaterMarkBitmap = createWaterMarkImage()

        val width = CameraParams.instance.mPreviewWidth
        val height = CameraParams.instance.mPreviewHeight

        val widthRatio = mBitmapWidth / width
        val heightRatio = mBitmapHeight / height

        val vertexFa = floatArrayOf(1f - widthRatio, -1f, 1f, -1f, 1f - widthRatio, -1f + heightRatio, 1f, -1f + heightRatio)
        mVertexFb = ByteBuffer.allocateDirect(vertexFa.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexFb?.clear()
        mVertexFb?.put(vertexFa)

        val textureFa = floatArrayOf(0f, 0f, 1f, 0f, 0f ,1f, 1f, 1f)
        mTextureFb = ByteBuffer.allocateDirect(textureFa.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTextureFb?.clear()
        mTextureFb?.put(textureFa)
    }

    private fun createWaterMarkImage(): Bitmap {
//        val bitmap = Bitmap.createBitmap(mBitmapWidth.toInt(), mBitmapHeight.toInt(), Bitmap.Config.RGB_565)
        val bitmap = Bitmap.createBitmap(mBitmapWidth.toInt(), mBitmapHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val rectF = RectF(0f, 0f, mBitmapWidth, mBitmapHeight)
        val paint = Paint()
        paint.color = Color.WHITE
        paint.isAntiAlias = true
        paint.textSize = DiyCameraKit.getPixels(25f).toFloat()
        paint.textAlign = Paint.Align.CENTER
        val waterMark = "水印"
        val fontMetrics = paint.fontMetrics
        canvas.drawText(waterMark, rectF.centerX(), rectF.centerY() - fontMetrics.bottom / 2f - fontMetrics.top / 2f , paint)
        return bitmap
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        mTypeHandle = GLES20.glGetUniformLocation(mProgramHandle, UNIFORM_TYPE)
    }

    override fun passShaderValue() {
        super.passShaderValue()
        GLES20.glUniform1i(mTypeHandle, 0)
    }

    override fun drawTexture(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer) {
        super.drawTexture(textureId, vertexFb, fragmentFb)

        GLES20.glEnable(GLES20.GL_BLEND)
//        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glBlendEquation(GLES20.GL_FUNC_ADD)
        drawWaterMark()
        GLES20.glDisable(GLES20.GL_BLEND)
    }

    private fun drawWaterMark() {
        if (mWaterMarkBitmap == null || mWaterMarkBitmap!!.isRecycled) {
            return
        }

        if (mWaterMarkTexture == 0) {
            mWaterMarkTexture = OpenGLUtils.ImageToTexture(mWaterMarkBitmap!!)
        }

        if (mWaterMarkTexture == 0) {
            return
        }

        GLES20.glUniform1i(mTypeHandle, 1)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mWaterMarkTexture)
        GLES20.glUniform1i(mTextureHandle, 0)

        mVertexFb?.position(0)
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mVertexFb)
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        mTextureFb?.position(0)
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureFb)
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        disableDrawArray()
    }

    override fun getFragmentShader(): String {
        return "precision mediump float;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +
                "uniform sampler2D $UNIFORM_INPUT_TEXTURE;\n" +
                "uniform int type;\n" +
                "void main() {\n" +
                "    if (type == 0) {\n" +
                "        gl_FragColor = texture2D($UNIFORM_INPUT_TEXTURE, $VARYING_TEXTURE);\n" +
                "    } else {\n" +
                "        gl_FragColor = texture2D($UNIFORM_INPUT_TEXTURE, vec2($VARYING_TEXTURE.x, 1.0 - $VARYING_TEXTURE.y));\n" +
                "    }\n" +
                "}\n"
    }
}