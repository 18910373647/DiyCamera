package com.xiang.sample.diycamera.utils.eventbus

import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class MainThreadSubscriber<T: BaseEvent>: BaseEventSubscriber() {

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMainThreadEvent(event: T) {

    }
}