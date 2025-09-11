package com.example.kmmreduxtemplate.core.di

import com.example.kmmreduxtemplate.core.network.KtorWebService
import com.example.kmmreduxtemplate.core.network.LogService
import com.example.kmmreduxtemplate.core.network.LogServiceImpl
import com.example.kmmreduxtemplate.core.redux.Action
import com.example.kmmreduxtemplate.core.redux.Effect
import com.example.kmmreduxtemplate.core.redux.ReduxStore
import com.example.kmmreduxtemplate.core.redux.Store
import com.example.kmmreduxtemplate.core.redux.app.AppMiddleware
import com.example.kmmreduxtemplate.core.redux.app.AppReducer
import com.example.kmmreduxtemplate.core.redux.app.AppState
import com.example.kmmreduxtemplate.core.redux.app.RootReducer
import com.example.kmmreduxtemplate.navigation.NavigationMiddleware
import com.example.kmmreduxtemplate.navigation.NavigationReducer
import com.example.kmmreduxtemplate.repository.MainNetworkApi
import com.example.kmmreduxtemplate.repository.MainNetworkApiImpl
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
                navigationReducer = get()
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
}

val apiModule = module {
    factory<MainNetworkApi> { MainNetworkApiImpl(get()) }
}

@ExperimentalTime
val serviceModule = module {
    single<LogService> { LogServiceImpl() }
    factory { KtorWebService(get()) }
}