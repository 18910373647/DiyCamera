package com.xiang.sample.diycameralibrary.filter.effect.rain

import android.opengl.GLES20
import com.xiang.sample.diycameralibrary.filter.base.BaseFilter

class DiyRainDropFilter: BaseFilter() {
    private var mTimeHandle = 0
    private var mStartTime = 0L

    override fun initShaderHandles() {
        super.initShaderHandles()
        mTimeHandle = GLES20.glGetUniformLocation(mProgramHandle, "time")
        mStartTime = System.currentTimeMillis()

    }

    override fun passShaderValue() {
        super.passShaderValue()

        var time = (System.currentTimeMillis() - mStartTime) / 1000f
        time %= 10
        GLES20.glUniform1f(mTimeHandle, time)
    }

    override fun getFragmentShader(): String {
        return "precision highp float;\n" +
                "varying vec2 $VARYING_TEXTURE;\n" +
                "uniform sampler2D $UNIFORM_INPUT_TEXTURE;\n" +
                "uniform float time;\n" +
                "const int MAX_RADIUS = 1;\n" +
                "\n" +
                hash12() +
                hash22() +
                "\n" +
                "void main() {\n" +
                "    float resolution = 8.0;\n" +
                "    vec2 uv = $VARYING_TEXTURE * resolution;\n" +
                "    vec2 p0 = floor(uv);\n" +

                "    vec2 circles = vec2(0.0);\n" +
                "    for (int j = -MAX_RADIUS; j <= MAX_RADIUS; ++j) {\n" +
                "        for (int i = -MAX_RADIUS; i <= MAX_RADIUS; ++i) {\n" +
                "            vec2 pi = p0 + vec2(float(i), float(j));\n" +
                "            vec2 hsh = pi;\n" +
                "            vec2 p = pi + hash22(hsh);\n" +

                "            float t = fract(0.4 * time + hash12(hsh));\n" +
                "            vec2 v = p - uv;\n" +
                "            float d = length(v) - (float(MAX_RADIUS) + 1.0) * t;\n" +

                "            float h = 1e-3;\n" +
                "            float d1 = d - h;\n" +
                "            float d2 = d + h;\n" +
                "            float p1 = sin(31.*d1) * smoothstep(-0.6, -0.3, d1) * smoothstep(0., -0.3, d1);\n" +
                "            float p2 = sin(31.*d2) * smoothstep(-0.6, -0.3, d2) * smoothstep(0., -0.3, d2);\n" +
                "            circles += 0.5 * normalize(v) * ((p2 - p1) / (2. * h) * (1. - t) * (1. - t));\n" +
                "        }\n" +
                "    }\n" +
                "    circles /= float((MAX_RADIUS * 2 + 1) * (MAX_RADIUS * 2 + 1));\n" +

                "    float intensity = mix(0.01, 0.15, smoothstep(0.1, 0.6, abs(fract(0.05*time + 0.5)*2.-1.)));\n" +
                "    vec3 n = vec3(circles, sqrt(1. - dot(circles, circles)));\n" +
                "    vec3 color = texture2D($UNIFORM_INPUT_TEXTURE, uv / resolution - intensity*n.xy).rgb + 5. * pow(clamp(dot(n, normalize(vec3(1., 0.7, 0.5))), 0., 1.), 6.);\n" +
                "    gl_FragColor = vec4(color, 1.0);\n" +
                "}\n"
    }

    private fun hash22(): String {
        return "vec2 hash22(vec2 p) {\n" +
                "   vec3 p3 = fract(vec3(p.xyx) * vec3(.1031, .1030, .0973));\n" +
                "   p3 += dot(p3, p3.yzx + 19.19);\n" +
                "   return fract((p3.xx + p3.yz) * p3.zy);\n" +
                "}\n"
    }

    private fun hash12(): String {
        return "float hash12(vec2 p) {\n" +
                "   vec3 p3  = fract(vec3(p.xyx) * 0.1031);\n" +
                "   p3 += dot(p3, p3.yzx + 19.19);\n" +
                "   return fract((p3.x + p3.y) * p3.z);\n" +
                "}\n"
    }
}