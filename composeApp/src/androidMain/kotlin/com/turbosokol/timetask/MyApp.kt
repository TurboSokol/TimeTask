package com.turbosokol.TimeTask

import android.app.Application
import com.turbosokol.TimeTask.di.initAndroidKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import kotlin.time.ExperimentalTime

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

class MyApp: Application(), KoinComponent {

    companion object {
        lateinit var instance: MyApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initKoin()
    }

    @OptIn(ExperimentalTime::class)
    private fun initKoin() {
        initAndroidKoin {
            androidContext(this@MyApp)
        }
    }


}