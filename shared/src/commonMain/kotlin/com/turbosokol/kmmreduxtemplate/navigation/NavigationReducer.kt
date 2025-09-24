package com.turbosokol.kmmreduxtemplate.navigation

import com.turbosokol.kmmreduxtemplate.core.redux.Action
import com.turbosokol.kmmreduxtemplate.core.redux.Reducer

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

class NavigationReducer : Reducer<NavigationState> {
    override fun reduce(oldState: NavigationState, action: Action): NavigationState {
        return when (action) {
            is NavigationAction.HomeScreen -> {
                oldState.copy(currentScreenState = action.homeScreenState)
            }
            else -> oldState
        }
    }
}