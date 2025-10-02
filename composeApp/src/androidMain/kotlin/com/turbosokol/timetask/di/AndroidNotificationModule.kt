/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.di

import com.turbosokol.TimeTask.notification.NotificationManager
import com.turbosokol.TimeTask.notification.TaskNotificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific notification module
 * Provides the actual TaskNotificationManager for Android platform
 */
val androidNotificationModule = module {
    single<NotificationManager> { 
        TaskNotificationManager(androidContext())
    }
}

