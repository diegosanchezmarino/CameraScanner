package com.blueit.barcodescanner

import android.Manifest

const val limitExtraKey = "limit"
const val barcodesExtraKey = "barcodesScanned"
const val barcodesToScanExtraKey = "barcodesScanned"



const val REQUEST_CODE_PERMISSIONS = 10
val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)


const val startScannerRequestCode = 101