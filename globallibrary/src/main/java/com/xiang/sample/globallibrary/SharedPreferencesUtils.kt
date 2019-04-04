package com.xiang.sample.globallibrary

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesUtils {
    private var sp: SharedPreferences? = null
    private val SP_NAME = "diy_camera"
    companion object {
        val instance: SharedPreferencesUtils by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SharedPreferencesUtils()
        }
    }

    private fun getSharedPreferences(): SharedPreferences {
        if (sp == null) {
            sp = DiyCameraKit.getAppContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        }
        return sp!!
    }

    fun getString(key: String, default: String): String {
        val sp = getSharedPreferences()
        return sp.getString(key, default) ?: default
    }

    fun commitString(key: String, value: String): Boolean {
        val sp = getSharedPreferences()
        return sp.edit().putString(key, value).commit()
    }

    fun applayString(key: String, value: String) {
        val sp = getSharedPreferences()
        sp.edit().putString(key, value).apply()
    }

    fun getInt(key: String, default: Int): Int {
        val sp = getSharedPreferences()
        return sp.getInt(key, default)
    }

    fun commitInt(key: String, value: Int): Boolean {
        val sp = getSharedPreferences()
        return sp.edit().putInt(key, value).commit()
    }

    fun applyInt(key: String, value: Int) {
        val sp = getSharedPreferences()
        return sp.edit().putInt(key, value).apply()
    }

    fun getFloat(key: String, default: Float): Float {
        val sp = getSharedPreferences()
        return sp.getFloat(key, default)
    }

    fun commitFloat(key: String, value: Float): Boolean {
        val sp = getSharedPreferences()
        return sp.edit().putFloat(key, value).commit()
    }

    fun applyFloat(key: String, value: Float) {
        val sp = getSharedPreferences()
        sp.edit().putFloat(key, value).apply()
    }

    fun getBoolean(key: String, default: Boolean): Boolean {
        val sp = getSharedPreferences()
        return sp.getBoolean(key, default)
    }

    fun commitBoolean(key: String, value: Boolean): Boolean {
        val sp = getSharedPreferences()
        return sp.edit().putBoolean(key, value).commit()
    }

    fun applyBoolean(key: String, value: Boolean) {
        val sp = getSharedPreferences()
        return sp.edit().putBoolean(key, value).apply()
    }

    fun getLong(key: String, default: Long): Long {
        val sp = getSharedPreferences()
        return sp.getLong(key, default)
    }

    fun commitLong(key: String, value: Long): Boolean {
        val sp = getSharedPreferences()
        return sp.edit().putLong(key, value).commit()
    }

    fun applyLong(key: String, value: Long) {
        val sp = getSharedPreferences()
        sp.edit().putLong(key, value).apply()
    }
}