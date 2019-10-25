package com.xiang.sample.diycamera.activity

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions

abstract class BaseActivity: AppCompatActivity() {
    protected var mRxPermission: RxPermissions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 去掉状态栏
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mRxPermission = RxPermissions(this)

        setContentView(getLayoutId())
        initView()
        initEvent()
        initData()
    }

    protected fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    abstract fun getLayoutId(): Int

    abstract fun initView()

    abstract fun initEvent()

    protected open fun initData() {

    }
}