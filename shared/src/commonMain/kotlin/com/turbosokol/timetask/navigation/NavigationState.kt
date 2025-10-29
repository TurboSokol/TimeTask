package com.turbosokol.TimeTask.navigation

import com.turbosokol.TimeTask.core.redux.Action
import com.turbosokol.TimeTask.core.redux.GeneralState
import com.turbosokol.TimeTask.screensStates.HomeScreenState

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

data class NavigationState(
    val currentScreenState: GeneralState
) : GeneralState {

    companion object {
        fun getInitState(): NavigationState = NavigationState(currentScreenState = HomeScreenState.getInitState())
    }

}

sealed class NavigationAction: Action {
    data class HomeScreen(val homeScreenState: HomeScreenState): NavigationAction()
}