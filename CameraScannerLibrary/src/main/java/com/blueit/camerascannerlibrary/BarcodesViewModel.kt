package com.blueit.camerascannerlibrary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blueit.camerascannerlibrary.model.CustomBarcode

class BarcodesViewModel : ViewModel() {


    var barcodesScanned: MutableLiveData<LinkedHashSet<CustomBarcode>> = MutableLiveData()
    var barcodesToScan: ArrayList<String> = ArrayList()

    init {
        restart()
    }

    fun restart() {
        barcodesScanned.value = LinkedHashSet()
    }

    fun getBarcodeAsStringArray(): ArrayList<String> {

        var result = ArrayList<String>()
        barcodesScanned.value?.forEach {
            result.add(it.rawValue)
        }

        return result
    }


    fun addBarcode(customBarcode: CustomBarcode) {


        if (barcodesToScan.any { it == customBarcode.rawValue })
            customBarcode.isCorrect = true


        var actualBarcodes = barcodesScanned.value!!
        if (actualBarcodes.add(customBarcode)) {
            barcodesScanned.value = actualBarcodes
        }

    }
}

