/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.di

import com.turbosokol.TimeTask.core.di.apiModule
import com.turbosokol.TimeTask.core.di.serviceModule
import com.turbosokol.TimeTask.core.di.storeModule
import com.turbosokol.TimeTask.core.di.repositoryModule
import com.turbosokol.TimeTask.core.di.lifecycleModule
import com.turbosokol.TimeTask.core.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import kotlin.time.ExperimentalTime

/**
 * Android-specific Koin initialization with notification support
 */
@OptIn(ExperimentalTime::class)
fun initAndroidKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        // Include shared modules from the shared module
        modules(
            // Load shared modules (including all required dependencies)
            storeModule,
            repositoryModule,
            databaseModule(),      // Platform-specific database module
            lifecycleModule,
            apiModule,
            serviceModule,
            // Add ComposeApp-specific modules
            appModule,
            // Add Android-specific notification module
            androidNotificationModule
        )
    }
}

