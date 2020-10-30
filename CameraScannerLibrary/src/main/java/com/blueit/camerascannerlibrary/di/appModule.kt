package com.blueit.camerascannerlibrary.di

import com.blueit.camerascannerlibrary.BarcodesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {


    viewModel { BarcodesViewModel() }
}