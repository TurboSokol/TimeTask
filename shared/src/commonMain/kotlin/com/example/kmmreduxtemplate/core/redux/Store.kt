package com.example.kmmreduxtemplate.core.redux

import com.example.kmmreduxtemplate.core.redux.app.AppState
import com.example.kmmreduxtemplate.core.redux.app.RootReducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

interface GeneralState
interface Action
interface Effect
object None : Action
object Empty : Effect

interface Store<S : GeneralState, A : Action, E : Effect> {
    fun observeState(): StateFlow<S>
    fun dispatch(action: A)
    fun getState(): S
    fun observeSideEffect(): Flow<E>
}

open class ReduxStore(
    private val reducer: RootReducer,
    defaultValue: AppState,
    private val middlewares: List<Middleware<AppState>>
): Store<AppState, Action, Effect>, CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private val state = MutableStateFlow(defaultValue)
    private val sideEffect = MutableSharedFlow<Effect>()

    override fun observeState(): StateFlow<AppState> = state.asStateFlow()

    override fun dispatch(action: Action) {
        val oldState = state.value
        val newState = reducer.reduce(oldState, action)

        middlewares.forEach { middleware ->
            launch {
                middleware.execute(newState, action, sideEffect).collect { middlewareAction ->
                    dispatch(middlewareAction)
                }
            }
        }

        if (newState != oldState) state.value = newState
    }

    override fun getState(): AppState = state.value

    override fun observeSideEffect(): Flow<Effect> = sideEffect
}