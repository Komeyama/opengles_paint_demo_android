package com.komeyama.opengles_paint_demo_android

class Line(
    override var drawOrder: ShortArray = shortArrayOf(0, 1, 2, 0, 2, 3),
    override var vertexCoordinates: FloatArray = floatArrayOf(
        -0.01f, +0.01f, 0.0f,
        -0.01f, -0.01f, 0.0f,
        +0.01f, -0.01f, 0.0f,
        +0.01f, +0.01f, 0.0f
    ),
    override val initColor: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
) : ShaderBase() {

    override fun vertexShaderCode(): String {
        return "" +
                "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = uMVPMatrix * vPosition;" +
                "}"
    }

    override fun fragmentShaderCode(): String {
        return "" +
                "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main(void){" +
                "   gl_FragColor = vColor;" +
                "}"
    }

    override fun handleFragmentParameter() {}

}