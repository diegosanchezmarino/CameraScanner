package com.blueit.barcodescanner.di

import com.blueit.barcodescanner.BarcodesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {


    viewModel { BarcodesViewModel() }
}