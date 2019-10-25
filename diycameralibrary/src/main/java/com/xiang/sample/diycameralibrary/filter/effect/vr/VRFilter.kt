package com.xiang.sample.diycameralibrary.filter.effect.vr

import android.content.res.AssetManager
import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import com.xiang.sample.diycameralibrary.utils.CameraParams
import com.xiang.sample.globallibrary.DiyCameraKit
import java.nio.FloatBuffer

class VRFilter: BaseFilter() {
    private var isInit = false

    init {
        System.loadLibrary("xgame")
    }

    private external fun nativeInit(width: Int, height: Int, assetManager: AssetManager)
    private external fun nativeStep()

    override fun onDrawFrameBuffer(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer): Int {
        return super.onDrawFrameBuffer(textureId, vertexFb, fragmentFb)
    }

    override fun drawTexture(textureId: Int, vertexFb: FloatBuffer, fragmentFb: FloatBuffer) {
        super.drawTexture(textureId, vertexFb, fragmentFb)

        if (!isInit) {
            nativeInit(CameraParams.instance.mPreviewWidth, CameraParams.instance.mPreviewHeight, DiyCameraKit.getAppContext().assets)
            isInit = true
        }
        nativeStep()
    }
}