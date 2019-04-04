package com.xiang.sample.diycamera.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.xiang.sample.diycamera.R
import com.xiang.sample.globallibrary.DiyCameraKit
import kotlinx.android.synthetic.main.diy_adjust_focus_view_layout.view.*

class DiyAdjustFocusPopup(context: Context): PopupWindow() {
    private var mAnimator: AnimatorSet? = null
    private var mHandler: Handler? = null

    init {
        initView(context)
        initAnimator()
        mHandler = Handler(Looper.getMainLooper())
    }

    private fun initView(context: Context) {
        contentView = LayoutInflater.from(context).inflate(R.layout.diy_adjust_focus_view_layout, null)
        width = DiyCameraKit.getPixels(120f)
        height = DiyCameraKit.getPixels(120f)
    }

    private fun initAnimator() {
        val animator = ObjectAnimator.ofFloat(contentView.diy_focus_img, "scaleX", 1f, 1.2f, 1f)
        val animator1 = ObjectAnimator.ofFloat(contentView.diy_focus_img, "scaleY", 1f, 1.2f, 1f)

        mAnimator = AnimatorSet()
        mAnimator!!.duration = 600
        mAnimator!!.play(animator).with(animator1)
    }

    private fun performAnimation() {
        if (mAnimator == null || mAnimator!!.isRunning) {
            return
        }

        mHandler?.removeCallbacksAndMessages(null)
        mHandler?.postDelayed({ dismiss() }, 1600)
        mAnimator!!.start()
    }

    fun show(parent: View, x: Float, y: Float) {
        val left = x - width / 2
        val top = y - height / 2
        super.showAtLocation(parent, Gravity.NO_GRAVITY, left.toInt(), top.toInt())

        performAnimation()

    }

    override fun dismiss() {
        super.dismiss()
        mHandler?.removeCallbacksAndMessages(null)
    }
}