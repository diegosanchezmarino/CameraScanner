package com.blueit.camerascannerlibrary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.scanner_lib_fragment_barcode_list.*


class BarcodeListFragment : Fragment() {

    val viewModel: BarcodesViewModel by lazy { ViewModelProvider(requireActivity()).get(BarcodesViewModel::class.java) }

    val adapter = BarcodesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        viewgroup: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.scanner_lib_fragment_barcode_list, viewgroup, false)


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(requireContext())


        viewModel.barcodesScanned.observe(viewLifecycleOwner, {
            adapter.updateData(it)
            recyclerview.scheduleLayoutAnimation()

            instruction.visibility = if (it.isEmpty()) VISIBLE else GONE
        })


    }


}