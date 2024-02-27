package vn.trunglt.democamera1

import android.view.SurfaceHolder

interface ICameraManager {
    val drawingView: DrawingView
    val surfaceHolder: SurfaceHolder
    fun onSurfaceCreated()
}