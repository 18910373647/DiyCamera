package com.xiang.sample.diycameralibrary.engine

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class SensorEventUtil(context: Context): SensorEventListener {
    private lateinit var mSensorManager: SensorManager
    private lateinit var mSensor: Sensor
    var mOrientation = -1

    init {
        mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val G = 9.81
        val SQRT2 = 1.414213
        if (event.sensor == null) {
            return
        }

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            if (z >= G / SQRT2) { //screen is more likely lying on the table
                if (x >= G / 2) {
                    mOrientation = 1
                } else if (x <= -G / 2) {
                    mOrientation = 2
                } else if (y <= -G / 2) {
                    mOrientation = 3
                } else {
                    mOrientation = 0
                }
            } else {
                if (x >= G / SQRT2) {
                    mOrientation = 1
                } else if (x <= -G / SQRT2) {
                    mOrientation = 2
                } else if (y <= -G / SQRT2) {
                    mOrientation = 3
                } else {
                    mOrientation = 0
                }
            }
        }
    }

}