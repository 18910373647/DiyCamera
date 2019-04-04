package com.xiang.sample.diycamera.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions
import com.xiang.sample.diycamera.R
import com.xiang.sample.diycamera.interfaces.IDiyTextureViewListener
import com.xiang.sample.diycamera.view.DiyAdjustFocusPopup
import com.xiang.sample.diycameralibrary.engine.PreviewManager
import com.xiang.sample.diycameralibrary.engine.RendererInputType
import com.xiang.sample.diycameralibrary.interfaces.ICameraFpsListener
import com.xiang.sample.diycameralibrary.interfaces.ICameraListener
import com.xiang.sample.diycameralibrary.utils.CameraParams
import kotlinx.android.synthetic.main.diy_camera_texture_view_preview_fragment_layout.*

class CameraTextureViewPreviewFragment: BaseFragment(), ICameraFpsListener, ICameraListener {
    private val mCameraParams: CameraParams by lazy { CameraParams.instance }
    private var mRxPermissions: RxPermissions? = null
    private var mAdjustFocusPopup: DiyAdjustFocusPopup? = null
    private val mRendererManager: PreviewManager by lazy { PreviewManager() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRxPermissions = RxPermissions(this)

        mRendererManager.setInputType(RendererInputType.INPUT_TEXTURE_VIEW)
            .setCameraFpsListener(this)
            .setCameraListener(this)
            .showFps(true)
            .start(activity!!)
    }

    override fun getLayoutId(): Int {
        return R.layout.diy_camera_texture_view_preview_fragment_layout
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        texture_view.setTargetAspect(mCameraParams.mCameraRatio)
        mRxPermissions!!.request(Manifest.permission.CAMERA).subscribe { openPreview(it) }
    }

    override fun initEvent() {
        texture_view.setListener(object: IDiyTextureViewListener {
            override fun onClick(event: MotionEvent) {
                val x = event.x
                val y = event.y
                showAdjustFocusView(x, y)
                mRendererManager.changeFocusOnArea(x, y)
            }

            override fun onDoubleClick() {
                mRendererManager.switchCamera()
            }
        })
    }

    private fun showAdjustFocusView(x: Float, y: Float) {
        if (mAdjustFocusPopup == null) {
            mAdjustFocusPopup = DiyAdjustFocusPopup(context!!)
        }

        if (mAdjustFocusPopup!!.isShowing) {
            mAdjustFocusPopup!!.dismiss()
        }

        mAdjustFocusPopup!!.show(texture_view, x, y)
    }

    private fun openPreview(hasPermission: Boolean) {
        if (!hasPermission) {
            Toast.makeText(context, "无相机权限", Toast.LENGTH_SHORT).show()
            return
        }
        mRendererManager.setTextureView(texture_view)
    }

    override fun onFpsChaned(fps: Float) {
        if (fps_tv.visibility != View.VISIBLE) {
            fps_tv.visibility = View.VISIBLE
        }
        fps_tv.text = "$fps"
    }

    override fun onCameraError(errno: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        mRendererManager.destroy()
    }
}