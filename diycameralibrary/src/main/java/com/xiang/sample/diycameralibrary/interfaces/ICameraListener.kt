package com.xiang.sample.diycameralibrary.interfaces

import com.xiang.sample.diycameralibrary.utils.CameraErrno

interface ICameraListener {
    fun onCameraError(@CameraErrno.Errno errno: Int)
}