package com.xiang.sample.diycamera.view

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xiang.sample.diycamera.R
import com.xiang.sample.diycameralibrary.utils.SpecialEffectFilterParams
import com.xiang.sample.globallibrary.DiyCameraKit
import kotlinx.android.synthetic.main.diy_special_effect_item_layout.view.*
import kotlinx.android.synthetic.main.diy_special_effect_view_layout.view.*

class DiySpecialEffectView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, def: Int = 0): ConstraintLayout(context, attr, def) {

    init {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.diy_special_effect_view_layout, this)

        recycler_view.layoutManager = LinearLayoutManager(context, OrientationHelper.HORIZONTAL, false)
        recycler_view.overScrollMode = View.OVER_SCROLL_NEVER
        recycler_view.addItemDecoration(object: RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                val position = parent?.getChildAdapterPosition(view) ?: return
                val total = parent.adapter?.itemCount ?: return

                if (position != 0) {
                    outRect?.left = DiyCameraKit.getPixels(15f)
                }

                if (position == total - 1) {
                    outRect?.right = DiyCameraKit.getPixels(10f)
                }
            }
        })
        recycler_view.adapter = DiySpecialEffectAdapter()
    }
}

class DiySpecialEffectAdapter: RecyclerView.Adapter<DiySpecialEffectViewHolder>() {
    private val mData: MutableList<SpecialEffectBean> = mutableListOf()

    init {
        mData.add(SpecialEffectBean(0, "无"))
        mData.add(SpecialEffectBean(1, "雨滴掉落"))
        mData.add(SpecialEffectBean(2, "雨滴滑落"))
        mData.add(SpecialEffectBean(3, "边框模糊"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiySpecialEffectViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.diy_special_effect_item_layout, null)
        return DiySpecialEffectViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: DiySpecialEffectViewHolder, position: Int) {
        val data = mData[position]
        holder.itemView.special_effect_nick_tv.text = data.nick

        val focus = SpecialEffectFilterParams.getSpecialEffectIndex()
        if (position == focus) {
            holder.itemView.special_effect_nick_tv.isSelected = true
            holder.itemView.special_effect_nick_tv.setTextColor(Color.WHITE)
        } else {
            holder.itemView.special_effect_nick_tv.isSelected = false
            holder.itemView.special_effect_nick_tv.setTextColor(Color.GRAY)
        }

        holder.itemView.setOnClickListener {
            notifyItemChanged(SpecialEffectFilterParams.getSpecialEffectIndex())
            SpecialEffectFilterParams.onSpecialEffectChanged(position)
            notifyItemChanged(SpecialEffectFilterParams.getSpecialEffectIndex())
        }
    }
}

class DiySpecialEffectViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

data class SpecialEffectBean(var position: Int, var nick: String)