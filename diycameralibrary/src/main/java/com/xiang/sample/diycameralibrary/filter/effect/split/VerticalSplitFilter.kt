package com.xiang.sample.diycameralibrary.filter.effect.split

import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VerticalSplitFilter(isTop: Boolean): BaseFilter() {
    private var mVertexFb: FloatBuffer ?= null
    private var mFragmentFb: FloatBuffer ?= null
    private var isTop = false

    init {
        this.isTop = isTop
    }

    public fun getTopVertexBuffer(): FloatBuffer {
        if (mVertexFb != null) {
            return mVertexFb!!
        }

        val buffer = floatArrayOf(-1f, 0f, 1f, 0f, -1f, 1f, 1f, 1f)
        mVertexFb = ByteBuffer
                            .allocateDirect(buffer.size * 4)
                            .order(ByteOrder.nativeOrder())
                            .asFloatBuffer()
        mVertexFb!!.clear()
        mVertexFb!!.put(buffer)
        mVertexFb!!.position(0)
        return mVertexFb!!
    }

    public fun getBottomVertexBuffer(): FloatBuffer {
        if (mVertexFb != null) {
            return mVertexFb!!
        }

        val buffer = floatArrayOf(-1f, -1f, 1f, -1f, -1f, 0f, 1f, 0f)
        mVertexFb = ByteBuffer
                            .allocateDirect(buffer.size * 4)
                            .order(ByteOrder.nativeOrder())
                            .asFloatBuffer()
        mVertexFb!!.clear()
        mVertexFb!!.put(buffer)
        mVertexFb!!.position(0)
        return mVertexFb!!
    }

    public fun getTextureCoordinateBuffer(): FloatBuffer {
        val buffer = floatArrayOf(0f, 0.25f, 1f, 0.25f, 0f, 0.75f, 1f, 0.75f)
        mFragmentFb = ByteBuffer
                        .allocateDirect(buffer.size * 4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer()
        mFragmentFb!!.clear()
        mFragmentFb!!.put(buffer)
        mFragmentFb!!.position(0)
        return mFragmentFb!!
    }

    override fun isUseVBO(): Boolean {
        return false
    }
}