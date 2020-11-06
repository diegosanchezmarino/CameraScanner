package com.blueit.camerascannerlibrary.viewholders

import android.view.View
import com.blueit.camerascannerlibrary.model.CustomBarcode
import kotlinx.android.synthetic.main.scanner_lib_cell_correct_barcode.view.*

class CorrectBarcodeViewHolder(itemView: View) : BarcodeViewHolder(itemView) {


    override fun bindView(customBarcode: CustomBarcode) {
        itemView.barcodeValue.text = customBarcode.rawValue
    }
}