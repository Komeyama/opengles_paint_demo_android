package com.komeyama.opengles_paint_demo_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button

class MainActivity : AppCompatActivity() {

    companion object {
        var displayWidth = 0
        var displayHeight = 0
    }

    lateinit var glView: MyGLSurface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getDeviceScreenSize()
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        glView = findViewById(R.id.my_gl_surface)
        val clearButton = findViewById<Button>(R.id.clear_button)
        val changeColorBlackButton = findViewById<Button>(R.id.change_black_button)
        val changeColorRedButton = findViewById<Button>(R.id.change_red_button)
        val changeColorGreenButton = findViewById<Button>(R.id.change_green_button)
        val changeColorBlueButton = findViewById<Button>(R.id.change_blue_button)

        clearButton.setOnClickListener {
            glView.queueEvent {
                glView.clear()
            }
        }

        changeColorBlackButton.setOnClickListener {
            changeColor(floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f))
        }

        changeColorRedButton.setOnClickListener {
            changeColor(floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f))
        }

        changeColorGreenButton.setOnClickListener {
            changeColor(floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f))
        }

        changeColorBlueButton.setOnClickListener {
            changeColor(floatArrayOf(0.0f, 0.0f, 1.0f, 1.0f))
        }
    }

    private fun changeColor(rgb: FloatArray) {
        glView.queueEvent {
            glView.changeColor(rgb)
        }
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