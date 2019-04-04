package com.xiang.sample.diycameralibrary.engine

import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.Message
import android.view.SurfaceHolder
import java.lang.ref.WeakReference

class PreviewRendererHandler(thread: PreviewRendererThread): Handler(thread.looper) {
    private var mPreviewRendererThread: WeakReference<PreviewRendererThread>? = null

    init {
        mPreviewRendererThread = WeakReference(thread)
    }

    override fun handleMessage(msg: Message?) {
        if (msg == null) {
            return
        }

        when (msg.what) {
            RendererMsgType.MSG_SURFACE_VIEW_CREATE -> mPreviewRendererThread?.get()?.openCamera(msg.obj as SurfaceHolder)

            RendererMsgType.MSG_SURFACE_VIEW_DESTROY -> mPreviewRendererThread?.get()?.releaseCamera()

            RendererMsgType.MSG_TEXTURE_VIEW_CREATE -> mPreviewRendererThread?.get()?.openCamera(msg.obj as SurfaceTexture)

            RendererMsgType.MSG_TEXTURE_VIEW_DESTROY -> mPreviewRendererThread?.get()?.releaseCamera()

            RendererMsgType.MSG_GL_SURFACE_VIEW_CREATE -> mPreviewRendererThread?.get()?.openCamera(msg.obj as SurfaceTexture)

            RendererMsgType.MSG_SWITCH_CAMERA -> mPreviewRendererThread?.get()?.switchCamera()

            RendererMsgType.MSG_FOCUS_ON_AREA -> mPreviewRendererThread?.get()?.focusAndMeteringOnArea(msg.arg1, msg.arg2)

            RendererMsgType.MSG_CALCULATE_FPS -> mPreviewRendererThread?.get()?.calculateFps()

            RendererMsgType.MSG_INIT_FACEPP -> mPreviewRendererThread?.get()?.initFacepp()

            RendererMsgType.MSG_INIT_RENDER_MANAGER -> RendererManager.instance.init(msg.obj as Context?)
        }
    }
}

object RendererMsgType {
    const val MSG_SURFACE_VIEW_CREATE = 0x000   // surfaceView可用
    const val MSG_SURFACE_VIEW_DESTROY = 0x001  // surfaceView destroy
    const val MSG_SURFACE_VIEW_SIZE_CHANGED = 0x002 // surfaceView size changed

    const val MSG_TEXTURE_VIEW_CREATE = 0x003   // textureView可用
    const val MSG_TEXTURE_VIEW_DESTROY = 0x004  // textureView destroy
    const val MSG_TEXTURE_VIEW_SIZE_CHANGED = 0x005 // textureView size changed

    const val MSG_GL_SURFACE_VIEW_CREATE = 0x007

    const val MSG_SWITCH_CAMERA = 0x008 // 切换摄像头

    const val MSG_FOCUS_ON_AREA = 0x009 // 区域聚焦

    const val MSG_CALCULATE_FPS = 0x010 // 计算fps

    const val MSG_INIT_FACEPP = 0x011 // 初始化face++

    const val MSG_INIT_RENDER_MANAGER = 0x012 // 初始化RenderManager
}