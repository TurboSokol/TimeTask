package com.example.kmmreduxtemplate.navigation

import com.example.kmmreduxtemplate.core.redux.Action
import com.example.kmmreduxtemplate.core.redux.Effect
import com.example.kmmreduxtemplate.core.redux.Middleware
import com.example.kmmreduxtemplate.core.redux.app.AppState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

class NavigationMiddleware : Middleware<AppState> {
    override suspend fun execute(
        state: AppState,
        action: Action,
        sideEffect: MutableSharedFlow<Effect>
    ): Flow<Action> {
        return emptyFlow()
    }
}