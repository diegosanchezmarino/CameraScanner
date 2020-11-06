package com.blueit.camerascannerlibrary

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.blueit.camerascannerlibrary.model.CustomBarcode
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.nio.ByteBuffer


class BarcodeAnalyser(private val listener: BarcodeListener) : ImageAnalysis.Analyzer {


    private val scanner = initBarcodeScanner()

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }


    private fun initBarcodeScanner(): BarcodeScanner {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_CODE_128
            ).build()
        return BarcodeScanning.getClient(options)
    }

//    override fun analyze(image: ImageProxy) {
//
//        val buffer = image.planes[0].buffer
//        val data = buffer.toByteArray()
//        val pixels = data.map { it.toInt() and 0xFF }
//        val luma = pixels.average()
//
//        listener(luma)
//
//        image.close()
//    }

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            // Pass image to an ML Kit Vision API
            // ...
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    // Task completed successfully
                    // ...
                    barcodes.forEach { barcode ->
                        barcode.rawValue?.let { rawValue ->
                            listener(CustomBarcode(rawValue))
                        }
                    }
                    imageProxy.close()
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                    it.toString()
                    imageProxy.close()
                }
        }

    }


}
