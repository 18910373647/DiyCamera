package com.xiang.sample.diycameralibrary.engine

import android.content.Context
import android.util.Base64
import android.util.Log
import com.megvii.facepp.sdk.Facepp
import com.megvii.licensemanager.sdk.LicenseManager
import com.xiang.sample.diycameralibrary.R
import com.xiang.sample.diycameralibrary.utils.CameraParams
import com.xiang.sample.globallibrary.SharedPreferencesUtils
import java.io.ByteArrayOutputStream
import java.util.*

/**
 *   public static class Face {
 *       public int trackID;       // 人脸的跟踪标记。
 *       public int index;         // 人脸数组下标
 *       public float confidence;  // 人脸置信度，为一个 0 ~ 1 之间的浮点数。
 *                                 // 超过 0.5 表示这确实是一个人脸。
 *
 *       public float pitch;    // 一个弧度，表示物体顺时针饶x轴旋转的弧度。
 *       public float yaw;      // 一个弧度，表示物体顺时针饶y轴旋转的弧度。
 *       public float roll;     // 一个弧度，表示物体顺时针饶z轴旋转的弧度。
 *
 *       public float[] leftEyestatus;  // 人左眼状态，每个数值表示概率，总和为 1
 *       public float[] rightEyestatus; // 人右眼状态，每个数值表示概率，总和为 1
 *       public float[] moutstatus;     // 嘴部状态
 *
 *       public float minority;         // 少数民族置信度（对于汉族而言）
 *       public float blurness;         // 模糊程度，数值越小表示越清晰，0 ~ 1
 *       public float age;              // 年龄，为浮点数
 *
 *       //男女概率之和为 1
 *       public float female;     // 是女性人脸的概率
 *       public float male;       // 是男性人脸的概率
 *
 *       public Rect rect;        // 人脸在图像中的位置，以一个矩形框来刻画。
 *       public PointF[] points;  // 人脸关键点信息。
 *       public byte[] feature;   // 人脸特征数据，务必保证其内存大小不低于feature_length
 *   }
 */
class FaceppHelper {
    private val mFacepp: Facepp by lazy { Facepp() }
    private var mSensorUtil: SensorEventUtil? = null
    private var mPitch = 0f
    private var mYaw = 0f
    private var mRoll = 0f
    var mLandmark = FloatArray(106 * 2) // 人脸关键点归一化
    var mLandmarkCoord = FloatArray(106 * 2)   // 人脸关键点绝对坐标
    var hasFace = false

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            FaceppHelper()
        }

        const val CN_LICENSE_URL = "https://api-cn.faceplusplus.com/sdk/v3/auth"
        const val FACEPP_UUID_KEY = "facepp_uuid"
        const val FACEPP_API_KEY = "UXluOIy5XK4qb2vEZ9URjgEUK99WlEWZ"
        const val FACEPP_API_SECRET = "qCsM9V9cGDSvpG39s6f6rUzbQVLOtYVC"
    }

    /**
     * face++验证
     */
    fun verify(context: Context, listener: IFaceppVerifyListener?) {
        val licenseManager = LicenseManager(context)
        val url = CN_LICENSE_URL
        val uuid = getUUID()
        val key = FACEPP_API_KEY
        val secret = FACEPP_API_SECRET
        val apiName = Facepp.getApiName()
        licenseManager.takeLicenseFromNetwork(url, uuid, key, secret, apiName, "1", object: LicenseManager.TakeLicenseCallback {
            override fun onFailed(errno: Int, bytes: ByteArray?) {
                if (bytes != null && bytes.isNotEmpty()) {
                    val msg = String(bytes)
                    Log.i("chengqixiang", "授权错误信息：$msg")
                }
                listener?.onVerifyFailed()
            }

            override fun onSuccess() {
                listener?.onVerifySuccess()
            }

        })
    }

    /**
     * face++初始化
     */
    fun init(context: Context) {
        mSensorUtil = SensorEventUtil(context)
        //  isOneFaceTrackig: 是否只检测一张脸? 是: 1; 否: 0。
        val errorCode = mFacepp.init(context, getFileContent(context, R.raw.megviifacepp_0_5_2_model), 1)
        if (errorCode != null) {
            Log.i("chengqixiang", "face++ 初始化失败 error === $errorCode")
            return
        }

        val faceppConfig = mFacepp.faceppConfig
        faceppConfig.interval = 30
        faceppConfig.minFaceSize = 40
        faceppConfig.roi_left = 0
        faceppConfig.roi_top = 0
        faceppConfig.roi_right = CameraParams.instance.mPreviewWidth
        faceppConfig.roi_bottom = CameraParams.instance.mPreviewHeight
        faceppConfig.detectionMode = Facepp.FaceppConfig.DETECTION_MODE_TRACKING_FAST
        mFacepp.faceppConfig = faceppConfig
    }

    /**
     * face++人脸识别关键点
     */
    fun detect(context: Context, data: ByteArray) {
        if (mSensorUtil == null) {
            mSensorUtil = SensorEventUtil(context)
        }

        if (mSensorUtil!!.mOrientation == -1) {
            return
        }

        val orientation = mSensorUtil!!.mOrientation
        val rotation = when (orientation) {
            1 -> 0
            2 -> 180
            3 -> CameraParams.instance.mCameraRotation
            else -> 360 - CameraParams.instance.mCameraRotation
        }

        val faceppConfig = mFacepp.faceppConfig
        if (faceppConfig.rotation != rotation) {
            faceppConfig.rotation = rotation
            mFacepp.faceppConfig = faceppConfig
        }

        val width = CameraParams.instance.mPreviewHeight
        val height = CameraParams.instance.mPreviewWidth
        val faces = mFacepp.detect(data, width, height, Facepp.IMAGEMODE_NV21)
        hasFace = faces != null && faces.isNotEmpty()
        if (hasFace) {
            // 只识别一张人脸
            val face = faces[0]
            mFacepp.getLandmarkRaw(face, Facepp.FPP_GET_LANDMARK106)
            mPitch = face.pitch
            mYaw = face.yaw
            mRoll = face.roll

            face.points.forEachIndexed { index, point ->
                mLandmarkCoord[index * 2] = point.y
                mLandmarkCoord[index * 2 + 1] = point.x

                mLandmark[index * 2] = 1 - point.y / height * 2
                mLandmark[index * 2 + 1] = point.x / width * 2 - 1
            }
        }
    }

    private fun getFileContent(context: Context, resource: Int): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var count: Int
        try {
            val inputStream = context.resources.openRawResource(resource)
            do {
                count = inputStream.read(buffer)
                if (count != -1) {
                    byteArrayOutputStream.write(buffer, 0, count)
                }
            } while (count != -1)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            byteArrayOutputStream.close()
        }

        return byteArrayOutputStream.toByteArray()
    }

    private fun getUUID(): String {
        var uuid = SharedPreferencesUtils.instance.getString(FACEPP_UUID_KEY, "")
        if (uuid.isNotEmpty()) {
            return uuid
        }

        uuid = UUID.randomUUID().toString()
        uuid = Base64.encodeToString(uuid.toByteArray(), Base64.DEFAULT)
        SharedPreferencesUtils.instance.applayString(FACEPP_UUID_KEY, uuid)
        return uuid
    }
}

interface IFaceppVerifyListener {
    fun onVerifySuccess()

    fun onVerifyFailed()
}

