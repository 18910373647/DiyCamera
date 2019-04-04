package com.xiang.sample.diycamera.utils.eventbus

import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 无论当前线程是在主线程还是子线程，事件回调都会新创建一个子线程
 */
abstract class AsyncThreadSubscriber<T: BaseEvent>: BaseEventSubscriber() {

    @Subscribe(threadMode = ThreadMode.ASYNC)
    open fun onAsyncThreadEvent(event: T) {

    }
}