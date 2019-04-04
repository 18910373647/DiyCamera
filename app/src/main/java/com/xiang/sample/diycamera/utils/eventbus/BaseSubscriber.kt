package com.xiang.sample.diycamera.utils.eventbus

import org.greenrobot.eventbus.EventBus

open class BaseEventSubscriber {

    fun register() {
        if (!isRegister()) {
            EventBus.getDefault().register(this)
        }
    }

    fun unregister() {
        if (isRegister()) {
            EventBus.getDefault().unregister(this)
        }
    }

    fun isRegister(): Boolean {
        return EventBus.getDefault().isRegistered(this)
    }
}