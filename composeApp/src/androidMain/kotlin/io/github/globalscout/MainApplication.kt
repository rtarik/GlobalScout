package io.github.globalscout

import android.app.Application
import io.github.globalscout.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MainApplication)
        }
    }
}