package com.turbosokol.TimeTask.core.di

import com.turbosokol.TimeTask.core.lifecycle.AppLifecycleManager
import com.turbosokol.TimeTask.core.network.KtorWebService
import com.turbosokol.TimeTask.core.network.LogService
import com.turbosokol.TimeTask.core.network.LogServiceImpl
import com.turbosokol.TimeTask.core.redux.Action
import com.turbosokol.TimeTask.core.redux.Effect
import com.turbosokol.TimeTask.core.redux.ReduxStore
import com.turbosokol.TimeTask.core.redux.Store
import com.turbosokol.TimeTask.core.redux.app.AppMiddleware
import com.turbosokol.TimeTask.core.redux.app.AppReducer
import com.turbosokol.TimeTask.core.redux.app.AppState
import com.turbosokol.TimeTask.core.redux.app.RootReducer
import com.turbosokol.TimeTask.navigation.NavigationMiddleware
import com.turbosokol.TimeTask.navigation.NavigationReducer
import com.turbosokol.TimeTask.repository.LocalTaskRepositoryImpl
import com.turbosokol.TimeTask.repository.MainNetworkApi
import com.turbosokol.TimeTask.repository.MainNetworkApiImpl
import com.turbosokol.TimeTask.repository.TaskRepository
import com.turbosokol.TimeTask.screensStates.HomeScreenMiddleware
import com.turbosokol.TimeTask.screensStates.HomeScreenReducer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/


@ExperimentalTime
fun initSharedKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(storeModule, repositoryModule, databaseModule(), lifecycleModule, apiModule, serviceModule)
}

// Expect platform-specific database module
expect fun databaseModule(): org.koin.core.module.Module

@ExperimentalTime
val storeModule = module {
    single<Store<AppState, Action, Effect>> {
        ReduxStore(
            reducer = RootReducer(
                appReducer = get(),
                navigationReducer = get(),
                homeScreenReducer = get()
            ),
            defaultValue = AppState(),
            middlewares = listOf(
                AppMiddleware(),
                NavigationMiddleware(),
                get<HomeScreenMiddleware>()
            )
        )
    }

    single { AppReducer() }
    single { NavigationReducer() }
    single { HomeScreenReducer() }
    single { HomeScreenMiddleware(get<TaskRepository>()) }
}

val repositoryModule = module {
    // Data sources - platform-specific implementations will be provided by databaseModule()
    // Note: RemoteTaskDataSource removed for now - will add back when remote is ready
    
    // Coroutine dispatcher
    single<CoroutineDispatcher> { Dispatchers.Default }
    
    // Repository - using local-only implementation for now
    single<TaskRepository> { 
        LocalTaskRepositoryImpl(
            localDataSource = get(),
            ioDispatcher = get()
        )
    }
}

val apiModule = module {
    factory<MainNetworkApi> { MainNetworkApiImpl(get()) }
}

val lifecycleModule = module {
    single<AppLifecycleManager> { AppLifecycleManager(get()) }
}

@ExperimentalTime
val serviceModule = module {
    single<LogService> { LogServiceImpl() }
    factory { KtorWebService(get()) }
}