package com.blueit.camerascannerlibrary

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.blueit.camerascannerlibrary.model.CustomBarcode
import kotlinx.android.synthetic.main.scanner_lib_activity_scanner_camera.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraScannerActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService


    val viewModel: BarcodesViewModel by lazy { ViewModelProvider(this).get(BarcodesViewModel::class.java) }

    var limit: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scanner_lib_activity_scanner_camera)

        val extras = intent.extras

        if (extras != null) {

            extras.get(limitExtraKey)?.let {
                limit = extras.getInt(limitExtraKey)
            }
            extras.get(barcodesToScanExtraKey)?.let {
                viewModel.barcodesToScan =
                    extras.getStringArrayList(barcodesToScanExtraKey) as ArrayList<String>
            }

            extras.get(barcodesToAvoidExtraKey)?.let {
                viewModel.barcodesToAvoid =
                    extras.getStringArrayList(barcodesToAvoidExtraKey) as ArrayList<String>
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
                amount.text = getString(R.string.scanner_lib_scanned, it.size.toString())
            } else {
                var missingAmount = viewModel.barcodesToScan.size - it.count { it.isCorrect }
                amount.text = resources.getQuantityString(
                    R.plurals.scanner_lib_missing_scans,
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
                    it.setAnalyzer(cameraExecutor,
                        BarcodeAnalyser { barcode ->
                            Log.d(TAG, "Barcode detected: ${barcode.rawValue}")
                            processNewBarcode(customBarcode = barcode)
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

    private fun processNewBarcode(customBarcode: CustomBarcode){


        if (customBarcode.rawValue.length > 20){
            Log.d(TAG, "Barcode: ${customBarcode.rawValue} is ${customBarcode.rawValue.length} characters long, more than 20 characters")
            return
        }

        if(viewModel.barcodesToAvoid.any{ it == customBarcode.rawValue}){
            Log.d(TAG, "Barcode: ${customBarcode.rawValue} has already been scanned before")
            return
        }



        limit?.let {

            var size = viewModel.barcodesScanned.value!!.size
            if (size < it)
                viewModel.addBarcode(customBarcode)
        } ?: run {
            viewModel.addBarcode(customBarcode)
        }
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
                    R.string.scanner_lib_missing_permissions_message,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXBasic"

        fun startScanner(
            activity: Activity,
            limitScan: Int? = null,
            valuesToScan: ArrayList<String>? = null,
            valuesToAvoid: ArrayList<String>? = null
        ) {
            var bundle = Bundle()


            limitScan?.let {
                bundle.putInt(limitExtraKey, it)
            }

            valuesToScan?.let {
                bundle.putStringArrayList(barcodesToScanExtraKey, it)
            }
            valuesToAvoid?.let {
                bundle.putStringArrayList(barcodesToAvoidExtraKey, it)
            }

            var intent = Intent(activity, CameraScannerActivity::class.java)
            intent.putExtras(bundle)
            startActivityForResult(activity, intent, startScannerRequestCode, null)
        }


    }
}

















































