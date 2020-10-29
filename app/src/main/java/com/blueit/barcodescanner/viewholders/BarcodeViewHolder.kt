package com.blueit.barcodescanner.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.blueit.barcodescanner.model.CustomBarcode

abstract class BarcodeViewHolder(itemView: View) : ViewHolder(itemView) {


    abstract fun bindView(customBarcode: CustomBarcode)
}