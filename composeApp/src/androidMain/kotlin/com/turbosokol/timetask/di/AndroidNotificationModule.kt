/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.di

import com.turbosokol.TimeTask.notification.NotificationManager
import com.turbosokol.TimeTask.service.TaskNotificationInteractor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * Android-specific notification module
 * Provides the actual TaskNotificationManager for Android platform
 * 
 * Note: TaskNotificationService is not provided here as Android instantiates it directly.
 * The service uses KoinComponent to inject dependencies.
 */
val androidNotificationModule = module {
    single<NotificationManager> { 
        TaskNotificationInteractor(
            context = androidApplication()
        )
    }
}

