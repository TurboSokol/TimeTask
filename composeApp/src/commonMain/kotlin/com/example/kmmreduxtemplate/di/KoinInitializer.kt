package com.example.kmmreduxtemplate.di

import com.example.kmmreduxtemplate.core.di.apiModule
import com.example.kmmreduxtemplate.core.di.serviceModule
import com.example.kmmreduxtemplate.core.di.storeModule
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
            // Load shared modules
            storeModule,
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