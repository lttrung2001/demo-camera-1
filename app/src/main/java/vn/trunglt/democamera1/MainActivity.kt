package vn.trunglt.democamera1

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import vn.trunglt.democamera1.databinding.ActivityMainBinding
import vn.trunglt.democamera1.managers.impl.CameraManager
import vn.trunglt.democamera1.managers.ICameraManager
import vn.trunglt.democamera1.managers.IScanQRManager
import vn.trunglt.democamera1.managers.impl.ScanQRManager
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val cameraManager by lazy {
        CameraManager()
    }
    private val scanQRManager by lazy {
        ScanQRManager()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()

        cameraManager.init()

        binding.btnTakePicture.setOnClickListener {
            cameraManager.swap()
        }
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
                cameraManager.openCamera(getFrontCameraId())
            }
        }
    }

    private fun getFrontCameraId(): Int {
        val availableCameras = Camera.getNumberOfCameras()
        val info = CameraInfo()
        for (i in 0 until availableCameras) {
            Camera.getCameraInfo(i, info)
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                return i
            }
        }
        return -1
    }

    private fun initListener() {
        cameraManager.setCallback(object : ICameraManager {
            override val drawingView = binding.transparentView
            override val surfaceHolder = binding.surfaceView.holder

            override fun onSurfaceCreated() {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), 0)
                } else {
                    cameraManager.openCamera(getFrontCameraId())
                }
            }

            override fun onAnalyze(inputImage: InputImage) {
                scanQRManager.scanQR(inputImage)
            }
        })
        scanQRManager.setCallback(object: IScanQRManager {
            override fun onSuccess(data: String?) {
                if (!data.isNullOrEmpty()) {
                    Toast.makeText(this@MainActivity, data, Toast.LENGTH_LONG).show()
                }
            }

            override fun onError(exception: Exception) {

            }

            override fun onComplete() {

            }
        })
    }
}