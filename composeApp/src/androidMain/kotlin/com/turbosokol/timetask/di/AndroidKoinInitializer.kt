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
import com.turbosokol.TimeTask.core.redux.Action
import com.turbosokol.TimeTask.core.redux.Effect
import com.turbosokol.TimeTask.core.redux.ReduxStore
import com.turbosokol.TimeTask.core.redux.Store
import com.turbosokol.TimeTask.core.redux.app.AppMiddleware
import com.turbosokol.TimeTask.core.redux.app.AppState
import com.turbosokol.TimeTask.core.redux.app.RootReducer
import com.turbosokol.TimeTask.navigation.NavigationMiddleware
import com.turbosokol.TimeTask.screensStates.HomeScreenMiddleware
import com.turbosokol.TimeTask.service.TimerService
import com.turbosokol.TimeTask.service.TimerUpdateService
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
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
            androidNotificationModule,
            // Add Android-specific timer service module
            androidTimerModule
        )
    }
}

/**
 * Android-specific timer service module
 */
val androidTimerModule = module {
    single { TimerService(androidContext()) }
    
    // Override the store module to include TimerService middleware
    single<Store<AppState, Action, Effect>> {
        val store = ReduxStore(
            reducer = RootReducer(
                appReducer = get(),
                navigationReducer = get(),
                homeScreenReducer = get()
            ),
            defaultValue = AppState(),
            middlewares = listOf(
                AppMiddleware(),
                NavigationMiddleware(),
                HomeScreenMiddleware(get()),
                get<TimerService>() // Add TimerService middleware
            )
        )
        
        // Initialize TimerUpdateService with the Redux store
        TimerUpdateService.initialize(store)
        
        store
    }
}

