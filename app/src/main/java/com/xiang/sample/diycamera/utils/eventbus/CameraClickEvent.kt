package com.xiang.sample.diycamera.utils.eventbus

import android.view.MotionEvent

class CameraClickEvent(e: MotionEvent): BaseEvent() {
    private var motionEvent: MotionEvent? = null

    init {
        motionEvent = e
    }

    fun getMotionEvent(): MotionEvent {
        return motionEvent!!
    }
}