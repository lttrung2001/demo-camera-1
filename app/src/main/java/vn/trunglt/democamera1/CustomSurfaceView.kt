package vn.trunglt.democamera1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import kotlin.math.pow
import kotlin.math.sqrt

class CustomSurfaceView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs) {
    private val faceRectList = mutableListOf<Rect>()
    private val rectBorderPaint by lazy {
        Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context, R.color.white)
            strokeWidth = 4f
        }
    }
    private val eraser by lazy {
        Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = ContextCompat.getColor(context, android.R.color.transparent)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(ContextCompat.getColor(context, android.R.color.transparent))
        drawFaces(canvas)
    }

    fun setFaceRectList(rectList: List<Rect>) {
        faceRectList.clear()
        faceRectList.addAll(rectList.map {
            val sx = measuredWidth / 2000
            val sy = measuredHeight / 2000

            it.set(
                (it.left + 1000) * sx,
                (it.top + 1000) * sx,
                (it.right + 1000) * sx,
                (it.bottom + 1000) * sx
            )
            it
        })
        postInvalidate()
    }

    private fun drawFaces(canvas: Canvas) {
        try {
            faceRectList.forEach {
                canvas.drawRect(it, rectBorderPaint)
            }
        } catch (e: Exception) {

        }
    }
}