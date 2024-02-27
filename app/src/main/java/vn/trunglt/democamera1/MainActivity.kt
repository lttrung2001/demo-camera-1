package vn.trunglt.democamera1

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toRect
import androidx.core.graphics.toRectF
import vn.trunglt.democamera1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {
    private lateinit var binding: ActivityMainBinding
    private var mCamera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.surfaceView.holder.addCallback(this)
        binding.surfaceView.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        binding.transparentView.holder.setFormat(PixelFormat.TRANSPARENT)
        binding.transparentView.holder.addCallback(this)
        binding.transparentView.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissions.indexOfFirst {
            it == Manifest.permission.CAMERA
        }.let {
            if (grantResults[it] == PackageManager.PERMISSION_GRANTED) {
                safeCameraOpen(getBackCameraId())
            }
        }
    }

    private fun safeCameraOpen(id: Int): Boolean {
        return try {
            releaseCameraAndPreview()
            mCamera = Camera.open(id)
            val parameters = mCamera?.parameters
            parameters?.previewFrameRate = 30
//            parameters?.setPreviewSize(1080, 1920)
//            parameters?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            mCamera?.parameters = parameters
            mCamera?.setDisplayOrientation(90)
            mCamera?.setPreviewDisplay(binding.surfaceView.holder)
            mCamera?.setFaceDetectionListener { faces, camera ->
                val faceRectList = faces.map { face ->
                    face.rect
                }
                binding.transparentView.setFaceRectList(faceRectList)
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

    private fun releaseCameraAndPreview() {
        mCamera?.also { camera ->
            camera.release()
            mCamera = null
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 0)
        } else {
            safeCameraOpen(getBackCameraId())
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        releaseCameraAndPreview()
    }

    private fun getBackCameraId(): Int {
        val availableCameras = Camera.getNumberOfCameras()
        val info = CameraInfo()
        for (i in 0 until availableCameras) {
            Camera.getCameraInfo(i, info)
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                return i
            }
        }
        return -1
    }
}