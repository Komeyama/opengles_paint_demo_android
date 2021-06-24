package com.komeyama.opengles_paint_demo_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics

class MainActivity : AppCompatActivity() {

    companion object {
        var displayWidth = 0
        var displayHeight = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getDeviceScreenSize()
        setContentView(R.layout.activity_main)
    }

    private fun getDeviceScreenSize() {
        val outMetrics = DisplayMetrics()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = this.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = this.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)
        }
        displayWidth = outMetrics.widthPixels
        displayHeight = outMetrics.heightPixels
    }
}