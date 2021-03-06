package com.xiang.sample.diycameralibrary.filter.effect.split

import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class HorizontalSplitFilter(isLeft: Boolean): BaseFilter() {
    private var isLeft = false
    private var mVertexFb: FloatBuffer ?= null
    private var mTextureFb: FloatBuffer ?= null

    init {
        this.isLeft = isLeft
    }

    public fun getLeftVertexFb(): FloatBuffer {
        if (mVertexFb != null) {
            return mVertexFb!!
        }

        val buffer = floatArrayOf(-1f, -1f, 0f, -1f, -1f, 1f, 0f, 1f)
        mVertexFb = ByteBuffer
                        .allocateDirect(buffer.size * 4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer()
        mVertexFb!!.clear()
        mVertexFb!!.put(buffer)
        mVertexFb!!.position(0)
        return mVertexFb!!
    }

    public fun getRightVertexFb(): FloatBuffer {
        if (mVertexFb != null) {
            return mVertexFb!!
        }

        val buffer = floatArrayOf(0f, -1f, 1f, -1f, 0f, 1f, 1f, 1f)
        mVertexFb = ByteBuffer
            .allocateDirect(buffer.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mVertexFb!!.clear()
        mVertexFb!!.put(buffer)
        mVertexFb!!.position(0)
        return mVertexFb!!
    }

    public fun getTextureFb(): FloatBuffer {
        if (mTextureFb != null) {
            return mTextureFb!!
        }

        val buffer = floatArrayOf(0.25f, 0f, 0.75f, 0f, 0.25f, 1f, 0.75f, 1f)
        mTextureFb = ByteBuffer
                        .allocateDirect(buffer.size * 4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer()
        mTextureFb!!.clear()
        mTextureFb!!.put(buffer)
        mTextureFb!!.position(0)
        return mTextureFb!!
    }

    override fun isUseVBO(): Boolean {
        return false
    }
}