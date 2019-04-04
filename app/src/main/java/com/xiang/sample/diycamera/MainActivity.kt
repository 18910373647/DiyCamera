package com.xiang.sample.diycamera

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.xiang.sample.diycamera.activity.BaseActivity
import com.xiang.sample.diycamera.activity.CameraPreviewActivity
import com.xiang.sample.diycameralibrary.engine.FaceppHelper
import com.xiang.sample.diycameralibrary.engine.IFaceppVerifyListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {
    private val mHandler by lazy { Handler(Looper.getMainLooper()) }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.welcome)
        video_view.setVideoURI(uri)
    }

    override fun initEvent() {
        video_view.setOnCompletionListener {
            video_view.start()
        }

        video_view.setOnErrorListener { mp, what, extra ->
            toPreviewActivity()
            true
        }

        video_view.setOnTouchListener { v, event ->
            if (video_view.isPlaying) {
                video_view.pause()
//                toPreviewActivity()
            }
            true
        }
    }

    override fun initData() {
        Thread(Runnable { verify() }).start()
    }

    private fun verify() {
        FaceppHelper.instance.verify(this, object: IFaceppVerifyListener {
            override fun onVerifySuccess() {
                mHandler.post { toPreviewActivity() }
            }

            override fun onVerifyFailed() {
                mHandler.post {
//                    showToast("face++ 鉴权失败")
                    toPreviewActivity()
                }
            }

        })
    }

    private fun toPreviewActivity() {
        val intent = Intent(this, CameraPreviewActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        video_view.start()
    }

    override fun onStop() {
        super.onStop()
        video_view.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        video_view.stopPlayback()
        mHandler.removeCallbacksAndMessages(null)
    }
}
