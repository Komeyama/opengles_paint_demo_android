package com.komeyama.opengles_paint_demo_android

import android.content.Context
import android.graphics.PointF
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent

class MyGLSurface(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs) {

    private var renderer: MyGLRenderer = MyGLRenderer(context)

    private var preTapPoint: PointF? = null
    private var tapPoint: PointF? = null
    private var displayWidth = 0.0f
    private var displayHeight = 0.0f


    init {
        this.setEGLContextClientVersion(2)
        this.setRenderer(renderer)
        displayWidth = MainActivity.displayWidth.toFloat()
        displayHeight = MainActivity.displayHeight.toFloat()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                tapPoint = PointF(event.x, event.y)
                if (preTapPoint == null) {
                    preTapPoint = tapPoint
                    return true
                } else {
                    val points = getLinePoints(preTapPoint!!, tapPoint!!)
                    renderer.points = points.map {
                        PointF(
                            (2 * it.x - displayWidth) / displayWidth,
                            (2 * it.y - displayHeight) / displayHeight
                        )
                    } as MutableList<PointF>
                    preTapPoint = tapPoint
                }
            }
            MotionEvent.ACTION_UP -> {
                preTapPoint = null
            }
        }
        return true
    }

    fun clear() {
        renderer.clear()
    }

    fun changeColor(rgb: FloatArray) {
        renderer.changeColor(rgb)
    }
}