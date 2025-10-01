package com.turbosokol.TimeTask.core.redux.app

import com.turbosokol.TimeTask.core.redux.Action
import com.turbosokol.TimeTask.core.redux.Reducer
import com.turbosokol.TimeTask.navigation.NavigationReducer
import com.turbosokol.TimeTask.screensStates.HomeScreenReducer

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

class AppReducer : Reducer<AppState> {
    override fun reduce(oldState: AppState, action: Action): AppState {
        return when (action) {
            is AppAction.SetPlatform -> {
                oldState.copy(appPlatform = action.platform)
            }

            else -> oldState
        }
    }

}

class RootReducer(
    private val appReducer: AppReducer,
    private val navigationReducer: NavigationReducer,
    private val homeScreenReducer: HomeScreenReducer
) : Reducer<AppState> {
    override fun reduce(oldState: AppState, action: Action): AppState = appReducer
        .reduce(oldState, action)
        .copy(
            navigationState = navigationReducer.reduce(oldState.navigationState, action),
            homeScreenState = homeScreenReducer.reduce(oldState.homeScreenState, action)
        )
}