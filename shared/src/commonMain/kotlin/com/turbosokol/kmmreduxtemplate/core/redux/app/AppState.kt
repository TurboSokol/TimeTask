package com.turbosokol.kmmreduxtemplate.core.redux.app

import com.turbosokol.kmmreduxtemplate.Platform
import com.turbosokol.kmmreduxtemplate.core.redux.Action
import com.turbosokol.kmmreduxtemplate.core.redux.GeneralState
import com.turbosokol.kmmreduxtemplate.getPlatform
import com.turbosokol.kmmreduxtemplate.navigation.NavigationState
import com.turbosokol.kmmreduxtemplate.screensStates.HomeScreenState

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
