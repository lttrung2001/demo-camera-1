package vn.trunglt.democamera1.managers.impl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.FaceDetectionListener
import android.util.Log
import android.view.SurfaceHolder
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import vn.trunglt.democamera1.managers.ICameraManager
import java.io.ByteArrayOutputStream


class CameraManager() : DefaultLifecycleObserver {
    private var cameraId: Int = 0
    private var mCamera: Camera? = null
    private var callback: ICameraManager? = null
    private val faceDetectionListener by lazy {
        FaceDetectionListener { faces, camera ->
            val faceRectList = faces.map { face -> face.rect }
            callback?.drawingView?.setFaceRectList(faceRectList)
        }
    }
    private val surfaceHolderCallback by lazy {
        object: SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                callback?.onSurfaceCreated()
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                release()
            }
        }
    }
    private val previewCallback by lazy {
        Camera.PreviewCallback { data, camera ->
            val parameters = camera.parameters
            val size = parameters.previewSize
            val width = size.width
            val height = size.height
            val yuv = YuvImage(data, parameters.previewFormat, width, height, null)
            val out = ByteArrayOutputStream()
            yuv.compressToJpeg(Rect(0, 0, width, height), 100, out)
            val bytes = out.toByteArray()
            val inputImage = InputImage.fromBitmap(getBitmapFrom(bytes), 90)
            callback?.onAnalyze(inputImage)
            camera.addCallbackBuffer(data)
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        setupDrawingView()
        setupPreviewView()
        Log.wtf("TRUNGLE", "onCreate")
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        resumeCamera()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        stopCamera()
        Log.wtf("TRUNGLE", "onStop")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        release()
    }

    fun init(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    fun setCallback(callback: ICameraManager) {
        this.callback = callback
    }

    fun openCamera(id: Int): Boolean {
        return try {
            release()
            mCamera = Camera.open(id)
            cameraId = id
            callback?.drawingView?.isMirror = id == CameraInfo.CAMERA_FACING_FRONT
            val parameters = mCamera?.parameters
            parameters?.previewFrameRate = 30
//            parameters?.setPreviewSize(1080, 1920)
//            parameters?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            mCamera?.parameters = parameters
            mCamera?.setDisplayOrientation(90)
            mCamera?.setPreviewDisplay(callback?.surfaceHolder)
            mCamera?.setPreviewCallback(previewCallback)
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

    private fun resumeCamera() {
        openCamera(cameraId)
    }

    private fun stopCamera() {
        release()
    }

    fun swap() {
        if (cameraId == CameraInfo.CAMERA_FACING_BACK) {
            openCamera(CameraInfo.CAMERA_FACING_FRONT)
        } else {
            openCamera(CameraInfo.CAMERA_FACING_BACK)
        }
    }

    private fun release() {
        mCamera?.setPreviewCallback(null)
        mCamera?.release()
        mCamera = null
    }

    private fun setupPreviewView() {
        callback?.surfaceHolder?.apply {
            addCallback(surfaceHolderCallback)
            setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
    }

    private fun setupDrawingView() {
        callback?.drawingView?.holder?.apply {
            setFormat(PixelFormat.TRANSPARENT)
            addCallback(surfaceHolderCallback)
            setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
    }

    private fun getBitmapFrom(data: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }
}