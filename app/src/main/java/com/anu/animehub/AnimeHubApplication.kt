package com.anu.animehub

import android.app.Application
import com.anu.animehub.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AnimeHubApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AnimeHubApplication)
            if (BuildConfig.DEBUG) androidLogger()
            modules(appModule)
        }
    }
}
