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

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val cameraManager by lazy {
        object: CameraManager() {
            override val drawingView = binding.transparentView
            override val surfaceHolder = binding.surfaceView.holder

            override fun onSurfaceCreated() {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), 0)
                } else {
                    openCamera(getBackCameraId())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraManager.init()
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
                cameraManager.openCamera(getBackCameraId())
            }
        }
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