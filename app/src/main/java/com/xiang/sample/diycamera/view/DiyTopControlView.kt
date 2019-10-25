package com.xiang.sample.diycamera.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.xiang.sample.diycamera.R
import kotlinx.android.synthetic.main.diy_top_control_view_layout.view.*

class DiyTopControlView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, def: Int = 0)
    : ConstraintLayout(context, attr, def), View.OnClickListener {

    init {
        initView()
        initEvent()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.diy_top_control_view_layout, this)
    }

    private fun initEvent() {
        top_control_setting_img.setOnClickListener(this)
        top_control_switch_img.setOnClickListener(this)
        top_control_album_img.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: return

        when (id) {
            top_control_setting_img.id -> showSettingDialog()
            top_control_switch_img.id -> switchCamera()
            top_control_album_img.id -> jumpAlbumActivity()
        }
    }

    private fun showSettingDialog() {

    }

    private fun switchCamera() {

    }

    private fun jumpAlbumActivity() {

    }
}