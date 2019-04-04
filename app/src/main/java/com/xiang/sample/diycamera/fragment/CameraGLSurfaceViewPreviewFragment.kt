package com.xiang.sample.diycamera.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions
import com.xiang.sample.diycamera.R
import com.xiang.sample.diycamera.utils.eventbus.CameraClickEvent
import com.xiang.sample.diycamera.utils.eventbus.CameraDoubleClickEvent
import com.xiang.sample.diycamera.utils.eventbus.MainThreadSubscriber
import com.xiang.sample.diycamera.view.DiyAdjustFocusPopup
import com.xiang.sample.diycameralibrary.engine.PreviewManager
import com.xiang.sample.diycameralibrary.engine.RendererInputType
import com.xiang.sample.diycameralibrary.interfaces.ICameraFpsListener
import com.xiang.sample.diycameralibrary.interfaces.ICameraListener
import com.xiang.sample.diycameralibrary.utils.CameraParams
import kotlinx.android.synthetic.main.diy_camera_gl_surface_view_preview_fragment_layout.*

class CameraGLSurfaceViewPreviewFragment: BaseFragment(), ICameraFpsListener, ICameraListener {
    private val mCameraParams: CameraParams by lazy { CameraParams.instance }
    private var mRxPermissions: RxPermissions? = null
    private var mAdjustFocusPopup: DiyAdjustFocusPopup? = null
    private val mRendererManager: PreviewManager by lazy { PreviewManager() }

    private val mScreenClickSubscriber = object: MainThreadSubscriber<CameraClickEvent>() {
        override fun onMainThreadEvent(event: CameraClickEvent) {
            val x = event.getMotionEvent().x
            val y = event.getMotionEvent().y
            showAdjustFocusView(x, y)
            mRendererManager.changeFocusOnArea(x, y)
        }
    }

    private val mScreenDoubleClickSubscriber = object: MainThreadSubscriber<CameraDoubleClickEvent>() {
        override fun onMainThreadEvent(event: CameraDoubleClickEvent) {
            mRendererManager.switchCamera()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRxPermissions = RxPermissions(this)
        mScreenClickSubscriber.register()
        mScreenDoubleClickSubscriber.register()

        mRendererManager.setInputType(RendererInputType.INPUT_GL_SURFACE_VIEW)
            .setCameraFpsListener(this)
            .setCameraListener(this)
            .showFps(true)
            .start(activity!!)
    }

    override fun getLayoutId(): Int {
        return R.layout.diy_camera_gl_surface_view_preview_fragment_layout
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        gl_surface_view.setTargetAspect(mCameraParams.mCameraRatio)
        mRxPermissions!!.request(Manifest.permission.CAMERA).subscribe { openPreview(it) }
    }

    override fun initEvent() {
    }

    private fun showAdjustFocusView(x: Float, y: Float) {
        if (mAdjustFocusPopup == null) {
            mAdjustFocusPopup = DiyAdjustFocusPopup(context!!)
        }

        if (mAdjustFocusPopup!!.isShowing) {
            mAdjustFocusPopup!!.dismiss()
        }

        mAdjustFocusPopup!!.show(gl_surface_view, x, y)
    }

    private fun openPreview(hasPermission: Boolean) {
        if (!hasPermission) {
            Toast.makeText(context, "无相机权限", Toast.LENGTH_SHORT).show()
            return
        }
        mRendererManager.setGLSurfaceView(gl_surface_view)
    }

    override fun onFpsChaned(fps: Float) {
        if (fps_tv?.visibility != View.VISIBLE) {
            fps_tv?.visibility = View.VISIBLE
        }
        fps_tv?.text = "$fps"
    }

    override fun onCameraError(errno: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()

        mScreenClickSubscriber.unregister()
        mScreenDoubleClickSubscriber.unregister()
        mRendererManager.destroy()

    }
}