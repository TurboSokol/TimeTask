package com.turbosokol.TimeTask.core.redux

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

interface Middleware<G : GeneralState> {
    suspend fun execute(
        state: G,
        action: Action,
        sideEffect: MutableSharedFlow<Effect>
    ): Flow<Action>
}
