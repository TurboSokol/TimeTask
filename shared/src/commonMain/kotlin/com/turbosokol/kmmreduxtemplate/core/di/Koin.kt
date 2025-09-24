package com.turbosokol.kmmreduxtemplate.core.di

import com.turbosokol.kmmreduxtemplate.core.network.KtorWebService
import com.turbosokol.kmmreduxtemplate.core.network.LogService
import com.turbosokol.kmmreduxtemplate.core.network.LogServiceImpl
import com.turbosokol.kmmreduxtemplate.core.redux.Action
import com.turbosokol.kmmreduxtemplate.core.redux.Effect
import com.turbosokol.kmmreduxtemplate.core.redux.ReduxStore
import com.turbosokol.kmmreduxtemplate.core.redux.Store
import com.turbosokol.kmmreduxtemplate.core.redux.app.AppMiddleware
import com.turbosokol.kmmreduxtemplate.core.redux.app.AppReducer
import com.turbosokol.kmmreduxtemplate.core.redux.app.AppState
import com.turbosokol.kmmreduxtemplate.core.redux.app.RootReducer
import com.turbosokol.kmmreduxtemplate.navigation.NavigationMiddleware
import com.turbosokol.kmmreduxtemplate.navigation.NavigationReducer
import com.turbosokol.kmmreduxtemplate.repository.MainNetworkApi
import com.turbosokol.kmmreduxtemplate.repository.MainNetworkApiImpl
import com.turbosokol.kmmreduxtemplate.screensStates.HomeScreenReducer
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
    modules(storeModule, apiModule, serviceModule)
}

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
                NavigationMiddleware()
            )
        )
    }

    single { AppReducer() }
    single { NavigationReducer() }
    single { HomeScreenReducer() }
}

val apiModule = module {
    factory<MainNetworkApi> { MainNetworkApiImpl(get()) }
}

@ExperimentalTime
val serviceModule = module {
    single<LogService> { LogServiceImpl() }
    factory { KtorWebService(get()) }
}