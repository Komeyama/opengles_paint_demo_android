package com.komeyama.opengles_paint_demo_android

import android.opengl.GLES20

class GLTexture {

    companion object {
        const val COORDINATES_PER_VERTEX = 3
        const val VERTEX_STRIDE = COORDINATES_PER_VERTEX * 4
        const val VERTEX_SHADER_SOURCE = "" +
                "uniform mat4 u_MvpMatrix;" +
                "attribute vec4 a_Position;" +
                "attribute vec2 a_TextureCoord;" +
                "varying vec2 v_TextureCoord;" +
                "" +
                "void main() {" +
                "   v_TextureCoord = a_TextureCoord;" +
                "   gl_Position = u_MvpMatrix * a_Position;" +
                "}"
        const val FRAGMENT_SHADER_SOURCE = "" +
                "uniform sampler2D u_Texture;" +
                "varying vec2 v_TextureCoord;" +
                "" +
                "void main() {" +
                "   gl_FragColor = texture2D(u_Texture, v_TextureCoord);" +
                "}";
    }

    private val vertexCoordinates: FloatArray = floatArrayOf(
        -1.0f, +1.0f, 0.0f,
        -1.0f, -1.0f, 0.0f,
        +1.0f, -1.0f, 0.0f,
        +1.0f, +1.0f, 0.0f
    )

    private val drawOrder: ShortArray = shortArrayOf(0, 1, 2, 0, 2, 3)

    private val textureCoordinates: FloatArray = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f
    )

    private var renderTexture = 0

    /**
     *  Buffers
     */
    private val vertexCoordinateBuffer by lazy {
        vertexCoordinates.allocateDirect()
    }

    private val drawListBuffer by lazy {
        drawOrder.allocateDirect()
    }

    private val textureCoordinateBuffer by lazy {
        textureCoordinates.allocateDirect()
    }

    /**
     * Handles
     */
    var programHandle = 0
    private var vPMatrixHandle = 0
    private var positionHandle = 0
    private var textureHandle = 0
    private var textureCoordinateHandle = 0

    init {

        val vertexShaderCode: String = VERTEX_SHADER_SOURCE
        val fragmentShaderCode: String = FRAGMENT_SHADER_SOURCE
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        programHandle = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        vPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MvpMatrix")
        positionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position")
        textureHandle = GLES20.glGetUniformLocation(programHandle, "u_Texture")
        textureCoordinateHandle = GLES20.glGetAttribLocation(programHandle, "a_TextureCoord")
    }

    fun initTexture(width: Int, height: Int, renderTexture: Int) {
        this.renderTexture = renderTexture
        val textureSize = width * height
        val texturePixelBuffer = textureSize.allocateDirect()

        // init texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.renderTexture)
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)

        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGB,
            width,
            height,
            0,
            GLES20.GL_RGB,
            GLES20.GL_UNSIGNED_SHORT_5_6_5,
            texturePixelBuffer
        )

    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle)

        GLES20.glVertexAttribPointer(
            positionHandle, COORDINATES_PER_VERTEX, GLES20.GL_FLOAT,
            false, VERTEX_STRIDE, vertexCoordinateBuffer
        )
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTexture)

        GLES20.glVertexAttribPointer(
            textureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0,
            textureCoordinateBuffer
        )
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES, drawOrder.size, GLES20.GL_UNSIGNED_SHORT,
            drawListBuffer
        )
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle)
    }

}