package com.blueit.barcodescanner

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.CameraX
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_scanner_camera.*
import kotlinx.android.synthetic.main.view_limit_reached.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraScannerActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService


    private val viewModel: BarcodesViewModel by viewModel()

    var limit: Int? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner_camera)

        val extras = intent.extras

        if (extras != null) {

            extras.get(limitExtraKey)?.let {
                limit = extras.getInt(limitExtraKey)
            }
            extras.get(barcodesToScanExtraKey)?.let {
                viewModel.barcodesToScan =
                    extras.getStringArrayList(barcodesToScanExtraKey) as ArrayList<String>
            }

        }


        viewModel.restart()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        viewModel.barcodesScanned.observe(this, Observer {
            fab.visibility = if (it.isEmpty()) GONE else VISIBLE


            if (viewModel.barcodesToScan.isEmpty()) {
                amount.text = getString(R.string.scanned, it.size.toString())
            } else {
                var missingAmount = viewModel.barcodesToScan.size - it.count { it.isCorrect }
                amount.text = resources.getQuantityString(
                    R.plurals.missing_scans,
                    missingAmount,
                    missingAmount.toString()
                )
            }
            if ((limit != null && it.size == limit)) {
                limitDialog.visibility = VISIBLE
            }

        })

        fab.setOnClickListener {
            applyScannedValues()
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.container, BarcodeListFragment())
            .commit()


        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun applyScannedValues() {
        val data = Intent()
        data.putExtra(barcodesExtraKey, viewModel.getBarcodeAsStringArray())
        setResult(RESULT_OK, data)
        finish()
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyser { barcode ->

                        limit?.let {

                            var size = viewModel.barcodesScanned.value!!.size
                            if (size < it)
                                viewModel.addBarcode(barcode)
                        } ?: run {
                            viewModel.addBarcode(barcode)
                        }


                        Log.d(TAG, "Barcode detected: ${barcode.rawValue}")
                    })
                }
            // Select back camera as a default
            val cameraSelector = DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    R.string.missing_permissions_message,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXBasic"

        fun startScanner(
            appCompatActivity: AppCompatActivity,
            limitScan: Int? = null,
            valuesToScan: ArrayList<String>? = null
        ) {
            var bundle = Bundle()


            limitScan?.let {
                bundle.putInt(limitExtraKey, it)
            }

            valuesToScan?.let {
                bundle.putStringArrayList(barcodesToScanExtraKey, it)
            }

            var intent = Intent(appCompatActivity, CameraScannerActivity::class.java)
            intent.putExtras(bundle)
            startActivityForResult(appCompatActivity, intent, startScannerRequestCode, null)
        }


    }
}

















































