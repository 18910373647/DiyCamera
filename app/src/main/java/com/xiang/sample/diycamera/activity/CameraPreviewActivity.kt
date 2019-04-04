package com.xiang.sample.diycamera.activity

import android.os.Process
import com.xiang.sample.diycamera.R
import com.xiang.sample.diycamera.fragment.CameraGLSurfaceViewPreviewFragment
import kotlinx.android.synthetic.main.diy_camera_preview_activity_layout.*

class CameraPreviewActivity: BaseActivity() {
//    private var mCameraPreviewFragment: CameraTextureViewPreviewFragment? = null
    private var mCameraPreviewFragment: CameraGLSurfaceViewPreviewFragment? = null

    override fun getLayoutId(): Int {
        return R.layout.diy_camera_preview_activity_layout
    }

    override fun initView() {
//        mCameraPreviewFragment = CameraTextureViewPreviewFragment()
//        supportFragmentManager
//            .beginTransaction()
//            .add(camera_preview_fragment_container.id, mCameraPreviewFragment!!, "CameraTextureViewPreviewFragment")
//            .commitAllowingStateLoss()

        mCameraPreviewFragment = CameraGLSurfaceViewPreviewFragment()
        supportFragmentManager
            .beginTransaction()
            .add(camera_preview_fragment_container.id, mCameraPreviewFragment!!, "CameraTextureViewPreviewFragment")
            .commitAllowingStateLoss()
    }

    override fun initEvent() {

    }

    override fun initData() {

    }

    override fun finish() {
        super.finish()
        Process.killProcess(Process.myPid())
    }
}