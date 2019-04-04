package com.xiang.sample.diycameralibrary.utils

import java.lang.annotation.Retention
import android.support.annotation.IntDef
import java.lang.annotation.RetentionPolicy

object CameraErrno {
    // open camera时，camera已经是打开状态
    const val ERRNO_ALREADY_INITILIALIZED = 0
    // camera不可用，getNumberOfCameras() <= 0
    const val ERRNO_INVALID = 1
    // camera前置摄像头不可用
    const val ERRNO_CAMERA_FRONT_INVALID = 2
    // camera后置摄像头不可用
    const val ERRNO_CAMERA_BACK_INVALID = 3
    // camera startPreview error
    const val ERRNO_CAMERA_PREVIEW_ERROR = 4
    // camera supportedPreviewSizes == null 或者 empty
    const val ERRNO_CAMERA_NO_SUPPORT_PREVIEW_SIZE = 5
    // camera switch error
    const val ERRNO_CAMERA_SWITCH = 6

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(ERRNO_ALREADY_INITILIALIZED, ERRNO_INVALID, ERRNO_CAMERA_FRONT_INVALID, ERRNO_CAMERA_BACK_INVALID,
            ERRNO_CAMERA_PREVIEW_ERROR, ERRNO_CAMERA_NO_SUPPORT_PREVIEW_SIZE, ERRNO_CAMERA_SWITCH)
    annotation class Errno
}