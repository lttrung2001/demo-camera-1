package vn.trunglt.democamera1.managers

import android.view.SurfaceHolder
import com.google.mlkit.vision.common.InputImage
import vn.trunglt.democamera1.views.DrawingView

interface ICameraManager {
    val drawingView: DrawingView
    val surfaceHolder: SurfaceHolder
    fun onSurfaceCreated()
    fun onAnalyze(inputImage: InputImage)
}