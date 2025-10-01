package com.turbosokol.TimeTask.core.redux.app

import com.turbosokol.TimeTask.Platform
import com.turbosokol.TimeTask.core.redux.Action
import com.turbosokol.TimeTask.core.redux.GeneralState
import com.turbosokol.TimeTask.getPlatform
import com.turbosokol.TimeTask.navigation.NavigationState
import com.turbosokol.TimeTask.screensStates.HomeScreenState

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

data class AppState(
    val appPlatform: Platform = getPlatform(),
    internal val navigationState: NavigationState = NavigationState.getInitState(),
    internal val homeScreenState: HomeScreenState = HomeScreenState.getInitState()
) : GeneralState {
    fun getNavigationState() = navigationState
    fun getHomeScreenState() = homeScreenState

}

sealed class AppAction : Action {
    data class SetPlatform(val platform: Platform) : AppAction()
}
