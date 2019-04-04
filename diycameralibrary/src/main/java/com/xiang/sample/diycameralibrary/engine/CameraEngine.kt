package com.xiang.sample.diycameralibrary.engine

import android.app.Activity
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.view.Surface
import android.view.SurfaceHolder
import com.xiang.sample.diycameralibrary.utils.CameraErrno
import com.xiang.sample.diycameralibrary.utils.CameraParams
import java.lang.Long.signum

class CameraEngine private constructor(): Camera.AutoFocusCallback {
    private var mCamera: Camera? = null

    companion object {
        val instance: CameraEngine by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            CameraEngine()
        }
    }

    fun openCamera(activity: Activity) {
        openCamera(activity, CameraParams.CAMERA_DEFAULT_ID)
    }

    fun openCamera(activity: Activity, cameraId: Int) {
        openCamera(activity, cameraId, CameraParams.CAMERA_DEFAULT_FPS)
    }

    fun openCamera(activity: Activity, cameraId: Int, expectFps: Int) {
        val width: Int
        val height: Int
        if (CameraParams.instance.mCameraRatio == CameraParams.CAMERA_RATIO_16_9) {
            width = CameraParams.CAMERA_DEFAULT_16_9_WIDTH
            height = CameraParams.CAMERA_DEFAULT_16_9_HEIGHT
        } else {
            width = CameraParams.CAMERA_DEFAULT_4_3_WIDTH
            height = CameraParams.CAMERA_DEFAULT_4_3_HEIGHT
        }
        openCamera(activity, cameraId, expectFps, width, height)
    }

    fun openCamera(activity: Activity, cameraId: Int, expectFps: Int, expectWidth: Int, expectHeight: Int) {
        if (mCamera != null) {
            CameraParams.instance.mCameraListener?.onCameraError(CameraErrno.ERRNO_ALREADY_INITILIALIZED)
            return
        }

        if (assertCameraId(cameraId)) {
            return
        }

        mCamera = Camera.open(cameraId)
        val params = CameraParams.instance
        params.mCameraId = cameraId
        val parameter = mCamera!!.parameters
        params.isCameraSupportFlash = checkSupportFlashLight(parameter)
        params.mCameraFps = choosePreviewFps(parameter, expectFps)
        params.mCameraFocusMode = chooseFocusMode(parameter)
        mCamera!!.parameters = parameter
        val rotation = calculatePreviewOrientation(activity)
        mCamera!!.setDisplayOrientation(rotation)
        setPreviewSize(mCamera!!, expectWidth, expectHeight)
        setPictureSize(mCamera!!, expectWidth, expectHeight)
    }

    fun setPreviewBuffer(cb: Camera.PreviewCallback, buffer: ByteArray) {
        mCamera?.setPreviewCallbackWithBuffer(cb)
        mCamera?.addCallbackBuffer(buffer)
    }

    /**
     * 开始预览
     */
    fun startPreview(texture: SurfaceTexture) {
        if (mCamera == null) {
            CameraParams.instance.mCameraListener?.onCameraError(CameraErrno.ERRNO_CAMERA_PREVIEW_ERROR)
            return
        }

        mCamera!!.setPreviewTexture(texture)
        mCamera!!.startPreview()
    }

    /**
     * 开始预览
     */
    fun startPreview(holder: SurfaceHolder) {
        if (mCamera == null) {
            CameraParams.instance.mCameraListener?.onCameraError(CameraErrno.ERRNO_CAMERA_PREVIEW_ERROR)
            return
        }
        mCamera!!.setPreviewDisplay(holder)
        mCamera!!.startPreview()
    }

    /**
     * 停止预览
     */
    fun stopPreview() {
        mCamera?.stopPreview()
    }

    /**
     * 释放相机
     */
    fun releaseCamera() {
        stopPreview()
        mCamera?.release()
        mCamera = null
    }

    /**
     * 检查当前摄像头是否可用
     */
    private fun assertCameraId(cameraId: Int): Boolean {
        var invalid = Camera.getNumberOfCameras() <= 0
        if (invalid) {
            CameraParams.instance.mCameraListener?.onCameraError(CameraErrno.ERRNO_INVALID)
            return invalid
        }

        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        invalid = info.facing != cameraId
        if (invalid && cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // 前置摄像头不可用
            CameraParams.instance.mCameraListener?.onCameraError(CameraErrno.ERRNO_CAMERA_FRONT_INVALID)
        } else if (invalid && cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            // 后置摄像头不可用
            CameraParams.instance.mCameraListener?.onCameraError(CameraErrno.ERRNO_CAMERA_BACK_INVALID)
        }
        return invalid
    }

    /**
     * 检查camera是否支持闪光灯
     */
    private fun checkSupportFlashLight(parameter: Camera.Parameters): Boolean {
        if (parameter.flashMode == null) {
            return false
        }

        val modes = parameter.supportedFlashModes
        if (modes == null || modes.isEmpty()) {
            return false
        }

        if (modes.size == 1 && modes[0].equals(Camera.Parameters.FLASH_MODE_OFF)) {
            return false
        }

        return true
    }

    /**
     *  选择预览帧率
     */
    private fun choosePreviewFps(parameter: Camera.Parameters, fps: Int): Int {
        val ranges = parameter.supportedPreviewFpsRange

        for (range in ranges) {
            if (range[0] == range[1] && range[0] == fps) {
                parameter.setPreviewFpsRange(range[0], range[1])
                return fps
            }
        }

        val range = IntArray(2)
        parameter.getPreviewFpsRange(range)
        return if (range[0] == range[1]) {
            range[0] / 1000
        } else {
            (range[1] - range[0]) / 2 * 1000
        }
    }

    /**
     * 设置预览分辨率
     */
    private fun setPreviewSize(camera: Camera, width: Int, height: Int) {
        val parameter = camera.parameters
        val supportedPreviewSizes = parameter.supportedPreviewSizes
        if (supportedPreviewSizes == null || supportedPreviewSizes.isEmpty()) {
            CameraParams.instance.mCameraListener?.onCameraError(CameraErrno.ERRNO_CAMERA_NO_SUPPORT_PREVIEW_SIZE)
            return
        }

        val size = calculatePerfectSize(supportedPreviewSizes, width, height)
        parameter.setPreviewSize(size.width, size.height)


        if (CameraParams.instance.mCameraRotation == 90 || CameraParams.instance.mCameraRotation == 270) {
            CameraParams.instance.mPreviewWidth = size.height
            CameraParams.instance.mPreviewHeight = size.width
        } else {
            CameraParams.instance.mPreviewWidth = size.width
            CameraParams.instance.mPreviewHeight = size.height
        }

        camera.parameters = parameter
    }

    /**
     * 设置图片拍照分辨率
     */
    private fun setPictureSize(camera: Camera, width: Int, height: Int) {
        val parameter = camera.parameters
        val size = calculatePerfectSize(parameter.supportedPictureSizes, width, height)
        parameter.setPictureSize(size.width, size.height)
        camera.parameters = parameter
    }

    /**
     * 计算预览和图片分辨率
     */
    private fun calculatePerfectSize(supportSize: List<Camera.Size>, width: Int, height: Int): Camera.Size {
        val bigEnoughList = arrayListOf<Camera.Size>()
        val noBigEnoughList = arrayListOf<Camera.Size>()

        // 1, 先找到等比例的preview size
        supportSize.forEach {
            if (it.width * width == it.height * height) { // 同比例
                if (it.width > width && it.height > height) {
                    bigEnoughList.add(it)
                } else {
                    noBigEnoughList.add(it)
                }
            }
        }

        // 2，先查找等比例下，小分辨率的 并且 比例 > 0.8。然后查找大分辨率
        var result: Camera.Size? = null
        if (noBigEnoughList.isNotEmpty()) {
            val size = noBigEnoughList.maxWith(Comparator { pre, next ->
                signum(pre.width * pre.height.toLong() - next.width * next.height.toLong())
            })

            if (size != null && size.width.toFloat() / height >= 0.8 && size.height.toFloat() / width > 0.8) {
                result = size
            }
        }

        // 3, 然后查找等比例下，大分辨率 并且 比例 > 0.8
        if (result == null && bigEnoughList.isNotEmpty()) {
            val size = bigEnoughList.minWith(Comparator { pre, next ->
                signum(pre.width * pre.height.toLong() - next.width * next.height.toLong())
            })

            if (size != null && height.toFloat() / size.width >= 0.8 && width.toFloat() / size.height >= 0.8) {
                result = size
            }
        }

        // 4，如果没有查找到合适的，查找宽度或者高度最接近的
        if (result == null) {
            result = supportSize[0]
            var isWidthOrHeightEqual = false

            for (size in supportSize) {
                if (size.width == height && size.height == width && size.height.toFloat() / size.width == CameraParams.instance.mCameraRatio) {
                    result = size
                    break
                } else if (size.height == width) {
                    isWidthOrHeightEqual = true
                    if (Math.abs(result!!.width - height) > Math.abs(size.width - height) && size.height.toFloat() / size.width == CameraParams.instance.mCameraRatio) {
                        result = size
                        continue
                    }
                } else if (size.width == height) {
                    isWidthOrHeightEqual = true
                    if (Math.abs(result!!.height - width) > Math.abs(size.height - width) && size.height.toFloat() / size.width == CameraParams.instance.mCameraRatio) {
                        result = size
                        continue
                    }
                } else if (!isWidthOrHeightEqual) {
                    if (Math.abs(result!!.width - height) > Math.abs(size.width - height)
                        && Math.abs(result.height - width) > Math.abs(size.width - height)
                        && size.height.toFloat() / size.width == CameraParams.instance.mCameraRatio) {
                        result = size
                        continue
                    }
                }
            }
        }

        return result!!
    }

    /**
     * 计算camera 旋转角度
     */
    private fun calculatePreviewOrientation(activity: Activity): Int {
        val orientation = activity.windowManager.defaultDisplay.orientation
        val degree = when (orientation) {
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }

        var result: Int
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(CameraParams.instance.mCameraId, info)
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degree) % 360
            result = (360 - result) % 360
        } else {
            result = (info.orientation - degree + 360) % 360
        }
        CameraParams.instance.mCameraRotation = result
        return result
    }

    /**
     * 选择聚焦模式
     */
    private fun chooseFocusMode(parameter: Camera.Parameters): String {
        val supportFocusModes = parameter.supportedFocusModes

        val result = if (supportFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            Camera.Parameters.FOCUS_MODE_AUTO
        } else if (supportFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        } else {
            parameter.focusMode
        }
        parameter.focusMode = result
        return result
    }

    /**
     * 设置聚焦区域，进行聚焦
     */
    fun focusOnArea(rect: Rect) {
        val parameter = mCamera?.parameters ?: return

        val maxNumFocusAreas = parameter.maxNumFocusAreas
        if (maxNumFocusAreas <= 0) {
            return
        }

        val supportFocusModes = parameter.supportedFocusModes
        if (!supportFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            return
        }

        if (parameter.focusMode != Camera.Parameters.FOCUS_MODE_AUTO) {
            parameter.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
        }

        val focusAreas = arrayListOf<Camera.Area>()
        focusAreas.add(Camera.Area(rect, 1000))
        parameter.focusAreas = focusAreas
        mCamera?.cancelAutoFocus()
        mCamera?.parameters = parameter
        mCamera?.autoFocus(this)
    }

    fun meteringLightOnArea(rect: Rect) {
        val parameter = mCamera?.parameters ?: return

        val maxNumMeteringAreas = parameter.maxNumMeteringAreas
        if (maxNumMeteringAreas <= 0) {
            return
        }

        val focusAreas = arrayListOf<Camera.Area>()
        focusAreas.add(Camera.Area(rect, 1000))
        parameter.meteringAreas = focusAreas
        mCamera?.parameters = parameter
    }

    /**
     * camera 聚焦成功回调
     */
    override fun onAutoFocus(success: Boolean, camera: Camera?) {

    }
}