package com.turbosokol.TimeTask.di

import com.turbosokol.TimeTask.core.di.apiModule
import com.turbosokol.TimeTask.core.di.serviceModule
import com.turbosokol.TimeTask.core.di.storeModule
import com.turbosokol.TimeTask.core.di.repositoryModule
import com.turbosokol.TimeTask.core.di.lifecycleModule
import com.turbosokol.TimeTask.core.di.databaseModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import kotlin.time.ExperimentalTime

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

@OptIn(ExperimentalTime::class)
fun initComposeAppKoin(appDeclaration: KoinAppDeclaration = {}) {
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
            appModule
        )
    }
}

/**
 * Platform-agnostic Koin initialization for common use
 */
@OptIn(ExperimentalTime::class)
fun initKoinForCompose() {
    initComposeAppKoin()
}