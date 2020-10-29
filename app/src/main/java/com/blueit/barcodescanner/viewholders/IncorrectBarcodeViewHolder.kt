package com.blueit.barcodescanner.viewholders

import android.view.View
import com.blueit.barcodescanner.model.CustomBarcode
import kotlinx.android.synthetic.main.cell_incorrect_barcode.view.*

class IncorrectBarcodeViewHolder(itemView: View) : BarcodeViewHolder(itemView) {


    override fun bindView(customBarcode: CustomBarcode) {
        itemView.barcodeValue.text = customBarcode.rawValue
    }
}