package com.example.monopolyultimatebanker.utils

import android.content.Context
import androidx.camera.view.CameraController
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

object QrScanner {
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    val qrScanner = BarcodeScanning.getClient(options)
}