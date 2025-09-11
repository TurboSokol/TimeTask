package com.example.kmmreduxtemplate.core.redux.app

import com.example.kmmreduxtemplate.Platform
import com.example.kmmreduxtemplate.core.redux.Action
import com.example.kmmreduxtemplate.core.redux.GeneralState
import com.example.kmmreduxtemplate.getPlatform
import com.example.kmmreduxtemplate.navigation.NavigationState
import com.example.kmmreduxtemplate.screensStates.HomeScreenState

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
