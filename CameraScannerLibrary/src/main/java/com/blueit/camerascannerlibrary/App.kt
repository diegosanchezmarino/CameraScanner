package com.blueit.camerascannerlibrary

import android.app.Application
import com.blueit.camerascannerlibrary.di.appModule
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