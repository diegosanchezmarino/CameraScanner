package com.blueit.camerascanner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blueit.camerascannerlibrary.CameraScannerActivity
import com.blueit.camerascannerlibrary.barcodesExtraKey
import com.blueit.camerascannerlibrary.startScannerRequestCode
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scannerButton.setOnClickListener {
            startWithLimitListAndAvoidList()
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

    fun start() {
        CameraScannerActivity.startScanner(this)
    }

    fun startWithLimit() {
        CameraScannerActivity.startScanner(this, 2)
    }

    fun startWithList() {
        var list = ArrayList<String>()
        list.add("12345")
        CameraScannerActivity.startScanner(this, valuesToScan = list)
    }

    fun startWithLimitAndList() {
        var list = ArrayList<String>()
        list.add("12345")
        list.add("23456")
        list.add("34567")
        CameraScannerActivity.startScanner(this, 4, list)
    }

    fun startWithLimitListAndAvoidList() {

        var list = ArrayList<String>()
        list.add("12345")
        list.add("23456")
        list.add("34567")

        var avoidList = ArrayList<String>()
        avoidList.add("123")
        avoidList.add("456")

        CameraScannerActivity.startScanner(this, 4, list, avoidList)
    }



}