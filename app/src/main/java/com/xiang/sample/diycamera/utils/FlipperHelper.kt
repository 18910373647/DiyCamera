package com.xiang.sample.diycamera.utils

import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.plugins.crashreporter.CrashReporterPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.leakcanary.LeakCanaryFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sandbox.SandboxFlipperPlugin
import com.facebook.flipper.plugins.sandbox.SandboxFlipperPluginStrategy
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import com.xiang.sample.globallibrary.DiyCameraKit
import javax.annotation.Nullable

class FlipperHelper private constructor() {

    companion object {
        val instance: FlipperHelper by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            FlipperHelper()
        }
    }

    // 三星手机支持性不好
    fun init() {
        SoLoader.init(DiyCameraKit.getAppContext(), false)

        val client = AndroidFlipperClient.getInstance(DiyCameraKit.getAppContext())

        // network
        val networkFlipperPlugin = NetworkFlipperPlugin()
        val interceptor = FlipperOkhttpInterceptor(networkFlipperPlugin)
        client.addPlugin(networkFlipperPlugin)

        //Inspector插件
        client.addPlugin(InspectorFlipperPlugin(DiyCameraKit.getAppContext(), DescriptorMapping.withDefaults()))


        //Sandbox插件
        client.addPlugin(SandboxFlipperPlugin(object : SandboxFlipperPluginStrategy {

            @Nullable
            override fun getKnownSandboxes(): Map<String, String>? {
                return HashMap()
            }

            override fun setSandbox(@Nullable sandbox: String?) {

            }
        }))

        //SP插件
        client.addPlugin(SharedPreferencesFlipperPlugin(DiyCameraKit.getAppContext(), "flipper_shared_preference"))

        //LeakCanary插件
        client.addPlugin(LeakCanaryFlipperPlugin())

        //CrashReporter插件
        client.addPlugin(CrashReporterPlugin.getInstance())
        client.start()
    }
}