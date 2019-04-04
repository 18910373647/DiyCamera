package com.xiang.sample.diycamera.utils.eventbus

import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 如果当前线程在主线程，事件回调在子线程
 * 如果当前线程在子线程，事件回调在该子线程
 */
abstract class BackgroundThreadSubscriber<T: BaseEvent>: BaseEventSubscriber() {

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    open fun onBackgroundThreadEvent(event: T) {

    }
}