package com.blueit.camerascannerlibrary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blueit.camerascannerlibrary.model.CustomBarcode
import com.blueit.camerascannerlibrary.viewholders.BarcodeViewHolder
import com.blueit.camerascannerlibrary.viewholders.CorrectBarcodeViewHolder
import com.blueit.camerascannerlibrary.viewholders.IncorrectBarcodeViewHolder

class BarcodesAdapter : RecyclerView.Adapter<BarcodeViewHolder>() {

    private val barcodes: LinkedHashSet<CustomBarcode> = LinkedHashSet()


    override fun getItemViewType(position: Int): Int {


        return if (barcodes.toList().reversed()[position].isCorrect)
            R.layout.cell_correct_barcode
        else
            R.layout.cell_incorrect_barcode
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        return when (viewType) {
            R.layout.cell_correct_barcode -> CorrectBarcodeViewHolder(view)
            R.layout.cell_incorrect_barcode -> IncorrectBarcodeViewHolder(view)
            else -> throw Exception("Incorrect viewtype")
        }
    }

    override fun onBindViewHolder(holderCorrect: BarcodeViewHolder, position: Int) {
        holderCorrect.bindView(barcodes.toList().reversed()[position])
    }

    override fun getItemCount(): Int {
        return barcodes.size
    }

    fun updateData(barcodes: LinkedHashSet<CustomBarcode>) {
        this.barcodes.addAll(barcodes)
        notifyItemInserted(0)
    }
}