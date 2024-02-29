package vn.trunglt.democamera1.managers.impl

import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import vn.trunglt.democamera1.managers.IScanQRManager

class ScanQRManager {
    private var callback: IScanQRManager? = null
    private val scanner by lazy {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        // getClient() creates a new instance of the MLKit barcode scanner with the specified options
        BarcodeScanning.getClient(options)
    }

    fun setCallback(callback: IScanQRManager) {
        this.callback = callback
    }

    fun scanQR(inputImage: InputImage) {
        scanner.process(inputImage)
            .addOnSuccessListener { barcodeList ->
                val barcode = barcodeList.getOrNull(0)
                // `rawValue` is the decoded value of the barcode
                callback?.onSuccess(barcode?.rawValue)
            }
            .addOnFailureListener {
                // This failure will happen if the barcode scanning model
                // fails to download from Google Play Services
                callback?.onError(it)
            }.addOnCompleteListener {
                // When the image is from CameraX analysis use case, must
                // call image.close() on received images when finished
                // using them. Otherwise, new images may not be received
                // or the camera may stall.
                callback?.onComplete()
            }
    }
}