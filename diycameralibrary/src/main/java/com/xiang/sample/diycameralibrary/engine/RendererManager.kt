package com.xiang.sample.diycameralibrary.engine

import android.content.Context
import android.util.SparseArray
import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import com.xiang.sample.diycameralibrary.filter.beauty.DiyBeautyFaceFilter
import com.xiang.sample.diycameralibrary.filter.effect.DiySpecialEffectFilter
import com.xiang.sample.diycameralibrary.filter.input.DiyOESInputFilter
import com.xiang.sample.diycameralibrary.filter.landmark.DiyFaceLandmarkFilter
import com.xiang.sample.diycameralibrary.filter.lookup.DiyLookupFilter
import com.xiang.sample.diycameralibrary.filter.output.DiyDisplayFilter
import com.xiang.sample.diycameralibrary.filter.watermark.DiyWaterMarkFilter
import com.xiang.sample.diycameralibrary.utils.OpenGLUtils
import com.xiang.sample.diycameralibrary.utils.TextureRotationUtils
import java.nio.FloatBuffer

class RendererManager {
    private var mVertexBuffer: FloatBuffer? = null
    private var mTextureBuffer: FloatBuffer? = null
    private var mDisplayVertexBuffer: FloatBuffer? = null
    private var mDisplayTextureBuffer: FloatBuffer? = null

    private val mFilters = SparseArray<BaseFilter>()

    companion object {
        val instance: RendererManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RendererManager()
        }
    }

    fun init(context: Context?) {
        initBuffers()
        initFilters(context)
    }

    private fun initBuffers() {
        releaseBuffers()

        mVertexBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.VERTEX_COORDS)
        mTextureBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.TEXTURE_COORDS)

        mDisplayVertexBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.VERTEX_COORDS)
        mDisplayTextureBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.TEXTURE_COORDS)
    }

    private fun releaseBuffers() {
        mVertexBuffer?.clear()
        mVertexBuffer = null

        mTextureBuffer?.clear()
        mTextureBuffer = null

        mDisplayVertexBuffer?.clear()
        mDisplayVertexBuffer = null

        mDisplayTextureBuffer?.clear()
        mDisplayTextureBuffer = null
    }

    private fun initFilters(context: Context?) {
        releaseFilters()

        mFilters.put(0, DiyOESInputFilter())
        mFilters.put(1, DiyBeautyFaceFilter())
        mFilters.put(2, DiyLookupFilter(context))
        mFilters.put(3, DiySpecialEffectFilter())
        mFilters.put(4, DiyWaterMarkFilter())
        mFilters.put(5, DiyDisplayFilter())
        mFilters.put(6, DiyFaceLandmarkFilter())
    }

    private fun releaseFilters() {
        val size = mFilters.size()

        for (index in 0 until size) {
            mFilters[index].release()
        }
        mFilters.clear()
    }

    fun renderer(textureId: Int, matrix: FloatArray) {
        var currentTexture = textureId
        if (mFilters[0] is DiyOESInputFilter) {
            (mFilters[0] as DiyOESInputFilter).setMatrix(matrix)
        }
        // 相机输入
        currentTexture = mFilters[0]?.drawFrameBuffer(currentTexture, mVertexBuffer!!, mTextureBuffer!!) ?: return
        // 美颜 磨皮
        currentTexture = mFilters[1]?.drawFrameBuffer(currentTexture, mVertexBuffer!!, mTextureBuffer!!) ?: return
        // lookup滤镜
        currentTexture = mFilters[2]?.drawFrameBuffer(currentTexture, mVertexBuffer!!, mTextureBuffer!!) ?: return
        // 特效
        currentTexture = mFilters[3]?.drawFrameBuffer(currentTexture, mVertexBuffer!!, mTextureBuffer!!) ?: return
        // 水印
        currentTexture = mFilters[4]?.drawFrameBuffer(currentTexture, mVertexBuffer!!, mTextureBuffer!!) ?: return
        // 相机输出
        mFilters[5]?.drawFrame(currentTexture, mDisplayVertexBuffer!!, mDisplayTextureBuffer!!)
        // 人脸关键点
//        mFilters[6]?.drawFrame(currentTexture, mVertexBuffer!!, mTextureBuffer!!)
    }
}