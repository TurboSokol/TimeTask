package com.example.kmmreduxtemplate.screensStates

import com.example.kmmreduxtemplate.core.redux.Action
import com.example.kmmreduxtemplate.core.redux.Reducer

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

class HomeScreenReducer : Reducer<HomeScreenState> {
    override fun reduce(oldState: HomeScreenState, action: Action): HomeScreenState {
        return when (action) {
            is HomeScreenAction.SetProgress -> {
                oldState.copy(inProgress = action.inProgress)
            }

            else -> oldState
        }
    }
}