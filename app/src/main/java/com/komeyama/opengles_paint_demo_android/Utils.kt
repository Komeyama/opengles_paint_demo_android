package com.komeyama.opengles_paint_demo_android

import android.graphics.Point
import android.graphics.PointF
import java.nio.*
import kotlin.math.abs
import kotlin.reflect.KFunction1

object ByteBufferUtils {
    fun <K : Buffer> allocateDirectBuffer(
        capacity: Int,
        bufferFormat: KFunction1<ByteBuffer, K>,
        innerBlock: (buffer: Buffer) -> Unit
    ): K {
        return ByteBuffer.allocateDirect(capacity).run {
            order(ByteOrder.nativeOrder())
            bufferFormat.invoke(this).apply {
                innerBlock.invoke(this)
                position(0)
            }
        }
    }
}

fun getLinePoints(p0: PointF, p1: PointF): List<PointF> {
    val points = mutableListOf<Point>()
    val pX0 = (p0.x * 1f).toInt()
    val pY0 = (p0.y * 1f).toInt()
    val pX1 = (p1.x * 1f).toInt()
    val pY1 = (p1.y * 1f).toInt()

    var x0: Int = pX0
    var y0: Int = pY0
    val x1: Int = pX1
    val y1: Int = pY1
    val dx: Int = (abs(pX1 - pX0))
    val dy: Int = (abs(pY1 - pY0))
    val sx: Int = if (pX1 > pX0) 1 else -1
    val sy: Int = if (pY1 > pY0) 1 else -1
    var err = dx - dy
    while (true) {
        if (x0 >= 0 && y0 >= 0) {
            points.add(Point(x0, y0))
        }
        if (x0 == x1 && y0 == y1) {
            break
        }
        val e2 = 2 * err
        if (e2 > -dy) {
            err -= dy
            x0 += sx
        }
        if (e2 < dx) {
            err += dx
            y0 += sy
        }
    }
    return points.map {
        PointF(
            it.x * 1f,
            it.y * 1f
        )
    }
}

fun ShortArray.allocateDirect(): ShortBuffer {
    return ByteBufferUtils.allocateDirectBuffer(this.size * 2, ByteBuffer::asShortBuffer) {
        (it as ShortBuffer).put(this)
    }
}

fun FloatArray.allocateDirect(): FloatBuffer {
    return ByteBufferUtils.allocateDirectBuffer(this.size * 4, ByteBuffer::asFloatBuffer) {
        (it as FloatBuffer).put(this)
    }
}

fun Int.allocateDirect(): IntBuffer {
    return ByteBufferUtils.allocateDirectBuffer(this * 4, ByteBuffer::asIntBuffer) {
        (it as IntBuffer).put(this)
    }
}
