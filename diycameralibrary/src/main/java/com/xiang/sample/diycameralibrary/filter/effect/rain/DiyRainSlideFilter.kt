package com.xiang.sample.diycameralibrary.filter.effect.rain

import android.opengl.GLES20
import com.xiang.sample.diycameralibrary.filter.base.BaseFilter
import com.xiang.sample.diycameralibrary.utils.CameraParams

class DiyRainSlideFilter: BaseFilter() {
    private var mResolutionHandle = 0
    private var mTimeHandle = 0

    private var mStartTime = 0L
    override fun initShaderHandles() {
        super.initShaderHandles()

        mResolutionHandle = GLES20.glGetUniformLocation(mProgramHandle, "resolution")
        mTimeHandle = GLES20.glGetUniformLocation(mProgramHandle, "time")

        mStartTime = System.currentTimeMillis()
    }

    override fun passShaderValue() {
        super.passShaderValue()

        val width = CameraParams.instance.mPreviewWidth
        val height = CameraParams.instance.mPreviewHeight
        GLES20.glUniform2f(mResolutionHandle, width.toFloat(), height.toFloat())

        val time = (System.currentTimeMillis() - mStartTime) / 1000f
        GLES20.glUniform1f(mTimeHandle, time)
    }

    override fun getFragmentShader(): String {
        return "precision highp float;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +
                "uniform sampler2D $UNIFORM_INPUT_TEXTURE;\n" +
                "uniform vec2 resolution;\n" +
                "uniform float time;\n" +
                "\n" +
                Saw() +
                N14() +
                N13() +
                N() +
                DropLayer2() +
                StaticDrops() +
                Drops() +
                "\n" +
                "void main() {\n" +
                "    vec2 uv = ($VARYING_TEXTURE * resolution - 0.5 * resolution) / resolution.y;\n" +
                "    vec2 UV = $VARYING_TEXTURE;\n" +
                "    float T = time;\n" +

                "    float t = T * .2;\n" +

                "    float rainAmount = sin(T * 0.05) * 0.3+ 0.7;\n" +

                "    float maxBlur = mix(3.0, 6.0, rainAmount);\n" +
                "    float minBlur = 1.0;\n" +

                "    float story = 0.0;\n" +
                "    float heart = 0.0;\n" +

                "    float zoom = 0.1;\n" +
                "    uv *= 0.7 + zoom * 0.3;\n" +

                "    UV = (UV - 0.5) * (0.9 + zoom * 0.1) + 0.5;\n" +

                "    float staticDrops = smoothstep(-0.5, 1.0, rainAmount) * 2.0;\n" +
                "    float layer1 = smoothstep(0.25, 0.75, rainAmount);\n" +
                "    float layer2 = smoothstep(0.0, 0.5, rainAmount);\n" +

                "    vec2 c = Drops(uv, t, staticDrops, layer1, layer2);\n" +
                "    vec2 e = vec2(0.001, 0.0);\n" +
                "    float cx = Drops(uv + e, t, staticDrops, layer1, layer2).x;\n" +
                "    float cy = Drops(uv + e.yx, t, staticDrops, layer1, layer2).x;\n" +
                "    vec2 n = vec2(cx - c.x, cy - c.x);\n" +

                "    float focus = mix(maxBlur - c.y, minBlur, smoothstep(0.1, 0.2, c.x));\n" +
//                "    vec3 col = texture2DLodEXT($UNIFORM_INPUT_TEXTURE, UV + n, focus).rgb;\n" +
                "    vec3 col = texture2D($UNIFORM_INPUT_TEXTURE, UV + n).rgb;\n" +
//
                "    gl_FragColor = vec4(col, 1.0);\n" +

//                "    gl_FragColor = texture2D($UNIFORM_INPUT_TEXTURE, $VARYING_TEXTURE);\n" +
                "}\n"
    }

    private fun Drops(): String {
        return "vec2 Drops(vec2 uv, float t, float l0, float l1, float l2) {\n" +
                "    float s = StaticDrops(uv, t) * l0; \n" +
                "    vec2 m1 = DropLayer2(uv, t) * l1;\n" +
                "    vec2 m2 = DropLayer2(uv * 1.85, t) * l2;\n" +

                "    float c = s + m1.x + m2.x;\n" +
                "    c = smoothstep(0.3, 1.0, c);\n" +

                "    return vec2(c, max(m1.y * l0, m2.y * l1));\n" +
                "}\n"
    }

    private fun StaticDrops(): String {
        return "float StaticDrops(vec2 uv, float t) {\n" +
               "    uv *= 40.;\n" +

               "    vec2 id = floor(uv);\n" +
               "    uv = fract(uv) - 0.5;\n" +
               "    vec3 n = N13(id.x * 107.45 + id.y * 3543.654);\n" +
               "    vec2 p = (n.xy - 0.5) * 0.7;\n" +
               "    float d = length(uv - p);\n" +

               "    float fade = Saw(0.025, fract(t + n.z));\n" +
               "    float c = smoothstep(0.3, 0.0, d) * fract(n.z * 10.0) * fade;\n" +
               "    return c;\n" +
               "}\n"
    }

    private fun DropLayer2(): String {
        return "vec2 DropLayer2(vec2 uv, float t) {\n" +
               "    vec2 UV = uv;\n" +

               "    uv.y += t * 0.75;\n" +
               "    vec2 a = vec2(6.0, 1.0);\n" +
               "    vec2 grid = a * 2.0;\n" +
               "    vec2 id = floor(uv * grid);\n" +

               "    float colShift = N(id.x); \n" +
               "    uv.y += colShift;\n" +

               "    id = floor(uv * grid);\n" +
               "    vec3 n = N13(id.x * 35.2 + id.y * 2376.1);\n" +
               "    vec2 st = fract(uv * grid) - vec2(0.5, 0.0);\n" +

               "    float x = n.x - 0.5;\n" +

               "    float y = UV.y * 20.0;\n" +
               "    float wiggle = sin(y + sin(y));\n" +
               "    x += wiggle * (0.5 - abs(x)) * (n.z - 0.5);\n" +
               "    x *= 0.7;\n" +
               "    float ti = fract(t + n.z);\n" +
               "    y = (Saw(0.85, ti) - 0.5) * 0.9 + 0.5;\n" +
               "    vec2 p = vec2(x, y);\n" +

               "    float d = length((st-p) * a.yx);\n" +

               "    float mainDrop = smoothstep(0.4, 0.0, d);\n" +

               "    float r = sqrt(smoothstep(1.0, y, st.y));\n" +
               "    float cd = abs(st.x - x);\n" +
               "    float trail = smoothstep(0.23 * r, 0.15 * r * r, cd);\n" +
               "    float trailFront = smoothstep(-0.02, 0.02, st.y - y);\n" +
               "    trail *= trailFront * r * r;\n" +

               "    y = UV.y;\n" +
               "    float trail2 = smoothstep(0.2 * r, 0.0, cd);\n" +
               "    float droplets = max(0.0, (sin(y * (1.0 - y) * 120.0) - st.y)) * trail2 * trailFront * n.z;\n" +
               "    y = fract(y * 10.0) + (st.y - 0.5);\n" +
               "    float dd = length(st - vec2(x, y));\n" +
               "    droplets = smoothstep(0.3, 0.0, dd);\n" +
               "    float m = mainDrop + droplets * r * trailFront;\n" +

               "    return vec2(m, trail);\n" +
               "}\n"
    }

    private fun N13(): String {
        return "vec3 N13(float p) {\n" +
                "   vec3 p3 = fract(vec3(p) * vec3(0.1031, 0.11369, 0.13787));\n" +
                "   p3 += dot(p3, p3.yzx + 19.19);\n" +
                "   return fract(vec3((p3.x + p3.y) * p3.z, (p3.x + p3.z) * p3.y, (p3.y + p3.z) * p3.x));\n" +
                "}"
    }

    private fun N14(): String {
        return "vec4 N14(float t) {\n" +
               "    return fract(sin(t * vec4(123.0, 1024.0, 1456.0, 264.0)) * vec4(6547.0, 345.0, 8799.0, 1564.0));\n" +
               "}\n"
    }

    private fun N(): String {
        return "float N(float t) {\n" +
               "   return fract(sin(t * 12345.564) * 7658.76);\n" +
               "}\n"
    }

    private fun Saw(): String {
        return "float Saw(float b, float t) {\n" +
               "    return smoothstep(0.0, b, t) * smoothstep(1.0, b, t);\n" +
               "}\n"
    }
}