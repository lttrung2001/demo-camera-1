package vn.trunglt.democamera1

import android.graphics.PixelFormat
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

abstract class CameraManager : DefaultLifecycleObserver,
    SurfaceHolder.Callback {
    private var mCamera: Camera? = null
    abstract val drawingView: DrawingView
    abstract val surfaceHolder: SurfaceHolder
    abstract fun onSurfaceCreated()

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        release()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        onSurfaceCreated()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        release()
    }

    fun init() {
        setupDrawingView()
        setupPreviewView()
    }

    fun openCamera(id: Int): Boolean {
        return try {
            release()
            mCamera = Camera.open(id)
            val parameters = mCamera?.parameters
            parameters?.previewFrameRate = 30
//            parameters?.setPreviewSize(1080, 1920)
//            parameters?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            mCamera?.parameters = parameters
            mCamera?.setDisplayOrientation(90)
            mCamera?.setPreviewDisplay(surfaceHolder)
            mCamera?.setFaceDetectionListener { faces, camera ->
                val faceRectList = faces.map { face ->
                    face.rect
                }
                drawingView.setFaceRectList(faceRectList)
            }
            mCamera?.startPreview()
            mCamera?.startFaceDetection()
            true
        } catch (e: Exception) {
            Log.wtf("TRUNGLE", "failed to open Camera")
            e.printStackTrace()
            false
        }
    }

    private fun release() {
        mCamera?.release()
        mCamera = null
    }

    private fun setupPreviewView() {
        surfaceHolder.apply {
            addCallback(this@CameraManager)
            setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
    }

    private fun setupDrawingView() {
        drawingView.holder?.apply {
            setFormat(PixelFormat.TRANSPARENT)
            addCallback(this@CameraManager)
            setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
    }
}