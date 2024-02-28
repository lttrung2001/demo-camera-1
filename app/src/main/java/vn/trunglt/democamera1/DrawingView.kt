package vn.trunglt.democamera1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRect
import androidx.core.graphics.toRectF


class DrawingView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs) {
    var isMirror = false
        set(value) {
            field = value
            faceRectList.clear()
            postInvalidate()
        }
    private val faceRectList = mutableListOf<Rect>()
    private val rectBorderPaint by lazy {
        Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context, R.color.white)
            strokeWidth = 4f
        }
    }
    private lateinit var matrix: Matrix

    init {
        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawFaces(canvas)
    }

    fun setFaceRectList(rectList: List<Rect>) {
        faceRectList.clear()
        faceRectList.addAll(rectList.map {
            val rectF = it.toRectF()
            if (!this::matrix.isInitialized) {
                matrix = Matrix().apply {
                    setScale(if (true) -1f else 1f, 1f)
                    postRotate(90f)
                    postScale(measuredWidth / 2000f, measuredHeight / 2000f)
                    postTranslate(measuredWidth / 2f, measuredHeight / 2f)
                }
            }
            matrix.mapRect(rectF)
            rectF.toRect()
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