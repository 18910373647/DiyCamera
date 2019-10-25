package com.xiang.sample.diycamera.view

import android.content.Context
import android.util.AttributeSet
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.xiang.sample.diycamera.R
import com.xiang.sample.diycamera.bean.LookupFilterBean
import com.xiang.sample.diycamera.interfaces.OnGestureListenerAdapter
import com.xiang.sample.diycamera.utils.eventbus.CameraClickEvent
import com.xiang.sample.diycamera.utils.eventbus.CameraDoubleClickEvent
import com.xiang.sample.diycameralibrary.utils.CameraParams
import com.xiang.sample.diycameralibrary.utils.LookupFilterParams
import kotlinx.android.synthetic.main.diy_lookup_filter_switcher_item_layout.view.*
import kotlinx.android.synthetic.main.diy_lookup_filter_switcher_layout.view.*
import org.greenrobot.eventbus.EventBus
import java.io.InputStreamReader

class LookupFilterSwitcher @JvmOverloads constructor(context: Context, attributes: AttributeSet? = null, def: Int = 0):
    ConstraintLayout(context, attributes, def) {

    init {
        initView()
        initEvent()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.diy_lookup_filter_switcher_layout, this)
        view_pager.adapter = LookupFilterSwitcherAdapter(context)
    }

    private fun initEvent() {
        view_pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                LookupFilterParams.onLookupFilterScrollState(state)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                val current = LookupFilterParams.getLookupFilterIndex()
                val isLeft = current == position
                LookupFilterParams.onLookupFilterOffset(isLeft, positionOffset)
            }

            override fun onPageSelected(position: Int) {
                LookupFilterParams.onLookupFilterChanged(position)
            }
        })
    }
}

class LookupFilterSwitcherAdapter(context: Context): PagerAdapter() {
    private var mList = arrayListOf<View>()
    private var mGesture: GestureDetector? = null

    private val mGestureListenerAdapter = object: OnGestureListenerAdapter() {
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            EventBus.getDefault().post(CameraDoubleClickEvent())
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            if (e == null) {
                return true
            }

            val x = e.x
            val y = e.y

            val width = CameraParams.instance.mViewWidth
            val height = CameraParams.instance.mViewHeight

            if (x >= 0 && x <= width && y >= 0 && y <= height) {
                EventBus.getDefault().post(CameraClickEvent(e))
            }
            return true
        }
    }

    init {
        mGesture = GestureDetector(mGestureListenerAdapter)
        mGesture!!.setOnDoubleTapListener(mGestureListenerAdapter)

        val inputStream = context.assets.open("lookup.json")
        val render = InputStreamReader(inputStream)
        val bean = Gson().fromJson(render, LookupFilterBean::class.java)
        LookupFilterParams.setLookupFilterCount(bean.getContent().size)

        bean.getContent().forEach {
            val view = LayoutInflater.from(context).inflate(R.layout.diy_lookup_filter_switcher_item_layout, null)
            handleItemTouchEvent(view)
            view.filter_nick_tv.text = it.getName()
            mList.add(view)
        }
    }

    private fun handleItemTouchEvent(view: View) {
        view.setOnTouchListener { v, event ->
            mGesture?.onTouchEvent(event) ?: false
        }
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val v = mList[position]
        container.addView(v)
        return v
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }
}