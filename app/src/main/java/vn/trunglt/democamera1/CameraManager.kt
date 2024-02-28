package vn.trunglt.democamera1

import android.graphics.PixelFormat
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.FaceDetectionListener
import android.util.Log
import android.view.SurfaceHolder
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

abstract class CameraManager : DefaultLifecycleObserver {
    private var cameraId: Int = 0
    private var mCamera: Camera? = null
    private val faceDetectionListener by lazy {
        FaceDetectionListener { faces, camera ->
            val faceRectList = faces.map { face -> face.rect }
            drawingView.setFaceRectList(faceRectList)
        }
    }
    private val surfaceHolderCallback by lazy {
        object: SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                onSurfaceCreated()
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                release()
            }
        }
    }
    abstract val drawingView: DrawingView
    abstract val surfaceHolder: SurfaceHolder
    abstract fun onSurfaceCreated()

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
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
            cameraId = id
            drawingView.isMirror = id == CameraInfo.CAMERA_FACING_FRONT
            val parameters = mCamera?.parameters
            parameters?.previewFrameRate = 30
//            parameters?.setPreviewSize(1080, 1920)
//            parameters?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            mCamera?.parameters = parameters
            mCamera?.setDisplayOrientation(90)
            mCamera?.setPreviewDisplay(surfaceHolder)
            mCamera?.setFaceDetectionListener(faceDetectionListener)
            mCamera?.startPreview()
            mCamera?.startFaceDetection()
            true
        } catch (e: Exception) {
            Log.wtf("TRUNGLE", "failed to open Camera")
            e.printStackTrace()
            false
        }
    }

    fun swap() {
        if (cameraId == CameraInfo.CAMERA_FACING_BACK) {
            openCamera(CameraInfo.CAMERA_FACING_FRONT)
        } else {
            openCamera(CameraInfo.CAMERA_FACING_BACK)
        }
    }

    private fun release() {
        mCamera?.release()
        mCamera = null
    }

    private fun setupPreviewView() {
        surfaceHolder.apply {
            addCallback(surfaceHolderCallback)
            setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
    }

    private fun setupDrawingView() {
        drawingView.holder?.apply {
            setFormat(PixelFormat.TRANSPARENT)
            addCallback(surfaceHolderCallback)
            setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
    }
}