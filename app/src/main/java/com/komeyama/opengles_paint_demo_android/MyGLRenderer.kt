package com.komeyama.opengles_paint_demo_android

import android.graphics.PointF
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {

    private var vPMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)

    private var frameBufferHandle = IntArray(1)
    private var renderTextureHandle = IntArray(1)

    private var textured: GLTexture? = null

    private val rotationMatrix = FloatArray(16)
    private val rotationMatrix2 = FloatArray(16)

    private var line: Line? = null

    var points = listOf<PointF>()

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        line = Line()
        textured = GLTexture()
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        homography(width, height)

        GLES20.glGenFramebuffers(1, frameBufferHandle, 0)
        GLES20.glGenTextures(1, renderTextureHandle, 0)
        textured?.initTexture(width, height, renderTextureHandle[0])
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle[0])
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D, renderTextureHandle[0], 0
        )
        GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)

        if (points.isNotEmpty()) {
            drawLine(points)
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        cameraViewConversion()

        textured?.let { GLES20.glUseProgram(it.programHandle) }
        drawQuad()
    }

    private fun drawQuad() {
        val scratch = FloatArray(16)
        val angle = 0f
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)
        textured?.draw(scratch)
    }

    private fun drawLine(p: List<PointF>) {
        val scratch = FloatArray(16)
        val angle = 0f
        val floatList = mutableListOf<Float>()
        val drawOrders = mutableListOf<Short>()
        var count = 0

        p.forEach {
            floatList.add(it.x)
            floatList.add(it.y)
            floatList.add(0.0f)
            drawOrders.add(count.toShort())
            count += 1
        }

        val floatArray = floatList.toTypedArray()
        line?.vertexCoordinates = floatArray.toFloatArray()
        line?.drawOrder = drawOrders.toTypedArray().toShortArray()

        if (drawOrders.toTypedArray().toShortArray().size > 1) {
            Matrix.setRotateM(rotationMatrix2, 0, angle, 0f, 0f, -1.0f)
            Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix2, 0)
            line?.draw(scratch)
            vPMatrix = FloatArray(16)
        }

    }

    private fun cameraViewConversion() {
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    private fun homography(width: Int, height: Int) {
        //val ratio: Float = width.toFloat() / height.toFloat()
        val ratio = 1.0f
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }
}