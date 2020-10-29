package com.blueit.barcodescanner

import android.app.Application
import com.blueit.barcodescanner.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(listOf(appModule))
        }

        instance = this


    }


    companion object {
        lateinit var instance: App
    }


}