package com.example.kmmreduxtemplate

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.example.kmmreduxtemplate.di.initComposeAppKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import kotlin.time.ExperimentalTime

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

class MyApp: Application(), Application.ActivityLifecycleCallbacks, KoinComponent {

    companion object {
        lateinit var instance: MyApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        registerActivityLifecycleCallbacks(this)

        initKoin()
    }

    @OptIn(ExperimentalTime::class)
    private fun initKoin() {
        initComposeAppKoin {
            androidContext(this@MyApp)
        }
    }

    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?
    ) {
        TODO("Not yet implemented")
    }

    override fun onActivityDestroyed(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityPaused(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityResumed(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle
    ) {
        TODO("Not yet implemented")
    }

    override fun onActivityStarted(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityStopped(activity: Activity) {
        TODO("Not yet implemented")
    }


}