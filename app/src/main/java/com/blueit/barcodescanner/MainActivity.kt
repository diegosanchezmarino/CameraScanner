package com.blueit.barcodescanner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scannerButton.setOnClickListener {
            startWithLimitAndList()
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === startScannerRequestCode) {
            if (resultCode === RESULT_OK) {
                val returnedResult = data?.getStringArrayListExtra(barcodesExtraKey)

                var message: StringBuilder = StringBuilder()
                returnedResult?.forEach {
                    message.append("$it ")
                }

                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun start(){
        CameraScannerActivity.startScanner(this)
    }

    fun startWithLimit(){
        CameraScannerActivity.startScanner(this,2)
    }

    fun startWithList(){
        var list = ArrayList<String>()
        list.add("7798051852282")
        CameraScannerActivity.startScanner(this,valuesToScan = list)
    }

    fun startWithLimitAndList(){
        var list = ArrayList<String>()
        list.add("7798051852282")
        CameraScannerActivity.startScanner(this,2,list)
    }




}