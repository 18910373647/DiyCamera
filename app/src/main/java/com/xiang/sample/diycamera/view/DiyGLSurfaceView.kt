package com.xiang.sample.diycamera.view

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import com.xiang.sample.diycamera.interfaces.IDiyTextureViewListener
import com.xiang.sample.diycamera.interfaces.OnGestureListenerAdapter

class DiyGLSurfaceView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null): GLSurfaceView(context, attr) {

    private var mTargetAspect = -1f
    private var mGesture: GestureDetector? = null
    private var mListener: IDiyTextureViewListener? = null

    private val mGestureListenerAdapter = object: OnGestureListenerAdapter() {
        override fun onDown(e: MotionEvent?): Boolean {
            return mListener != null
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            mListener?.onDoubleClick()
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            if (e != null) {
                mListener?.onClick(e)
            }
            return true
        }
    }

    init {
        mGesture = GestureDetector(mGestureListenerAdapter)
        mGesture!!.setOnDoubleTapListener(mGestureListenerAdapter)
    }

    fun setListener(listener: IDiyTextureViewListener) {
        mListener = listener
    }

    fun setTargetAspect(aspect: Float) {
        mTargetAspect = aspect
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpec1 = widthMeasureSpec
        var heightMeasureSpec1 = heightMeasureSpec

        if (mTargetAspect > 0) {
            var initWidth = MeasureSpec.getSize(widthMeasureSpec)

            val horizontal = paddingLeft + paddingRight
            val vertical = paddingTop + paddingBottom

            initWidth -= horizontal
            var initHeight = initWidth / mTargetAspect

            initWidth += horizontal
            initHeight += vertical
            widthMeasureSpec1 = MeasureSpec.makeMeasureSpec(initWidth, MeasureSpec.EXACTLY)
            heightMeasureSpec1 = MeasureSpec.makeMeasureSpec(initHeight.toInt(), MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec1, heightMeasureSpec1)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mGesture?.onTouchEvent(event) ?: true
    }
}