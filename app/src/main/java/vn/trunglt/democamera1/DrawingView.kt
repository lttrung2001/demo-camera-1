package vn.trunglt.democamera1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.util.AttributeSet
import android.view.SurfaceView
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRect
import androidx.core.graphics.toRectF


class DrawingView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs) {
    private val faceRectList = mutableListOf<Rect>()
    private val rectBorderPaint by lazy {
        Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context, R.color.white)
            strokeWidth = 4f
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
            val rectF = it.toRectF()
            val matrix = Matrix()
            matrix.setScale(if (true) -1f else 1f, 1f)
            // This is the value for android.hardware.Camera.setDisplayOrientation.
            // This is the value for android.hardware.Camera.setDisplayOrientation.
            matrix.postRotate(90f)
            // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
            // UI coordinates range from (0, 0) to (width, height).
            // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
            // UI coordinates range from (0, 0) to (width, height).
            matrix.postScale(measuredWidth / 2000f, measuredHeight / 2000f)
            matrix.postTranslate(measuredWidth / 2f, measuredHeight / 2f)
            matrix.mapRect(rectF)
            rectF.toRect()
        })
        invalidate()
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