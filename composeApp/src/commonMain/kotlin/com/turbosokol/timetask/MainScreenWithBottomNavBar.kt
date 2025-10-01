package com.turbosokol.TimeTask

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.turbosokol.TimeTask.core.redux.app.AppState
import com.turbosokol.TimeTask.localization.rememberPlatformLanguageDetector
import com.turbosokol.TimeTask.navigation.NavigationState
import com.turbosokol.TimeTask.screens.HomeScreen
import com.turbosokol.TimeTask.screensStates.HomeScreenState
import com.turbosokol.TimeTask.viewmodel.ReduxViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

@Preview
@Composable
fun MainScreenWithBottomNavBar() {
    // Initialize platform language detection at app level
    rememberPlatformLanguageDetector()
    
    val viewModel: ReduxViewModel = koinViewModel()

    val stateFlow: StateFlow<AppState> = viewModel.store.observeState()
    val appState by stateFlow.collectAsState(Dispatchers.Main)
    val navigationState: NavigationState = appState.getNavigationState()

    val selectedTab = when (navigationState.currentScreenState) {
        is HomeScreenState -> NavigationTabs.HOME
        else -> NavigationTabs.HOME
    }


    MaterialTheme {
        Scaffold(
//            bottomBar = {
//                Surface(
//                    shape = RoundedCornerShape(
//                        topStart = Dimensions.cornerMedium,
//                        topEnd = Dimensions.cornerMedium
//                    ),
//                    tonalElevation = Dimensions.elevationMedium
//                ) {
//                    NavigationBar(
//                        containerColor = MaterialTheme.colorScheme.primaryContainer,
//                        windowInsets = WindowInsets(5, 15, 5, 1),
//                        modifier = Modifier.height(Dimensions.navBarHeight)
//                    ) {
//
//                        val tabsList = listOf<NavigationTabs>(
//                            NavigationTabs.HOME
//                        )
//
//                        tabsList.forEach { tab ->
//                            val (title, icon, action) = when (tab) {
//                                NavigationTabs.HOME -> Triple(
//                                    "Home",
//                                    Icons.Outlined.Chair,
//                                    NavigationAction.HomeScreen(appState.getHomeScreenState())
//                                )
//                            }
//
//                            IQBottomNavItemView(
//                                modifier = Modifier,
//                                icon = { Icon(imageVector = icon, contentDescription = null) },
//                                label = {
//                                    Text(
//                                        text = title,
//                                        fontSize = Dimensions.textHeader3
//                                    )
//                                },
//                                selected = selectedTab == tab,
//                                alwaysShowLabel = true,
//                                onClick = { viewModel.execute(action) },
//                                selectedContentColor = MaterialTheme.colorScheme.tertiary,
//                                unselectedContentColor = MaterialTheme.colorScheme.onPrimary
//                            )
//                        }
//                    }
//                }
//
//            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (selectedTab) {
                    NavigationTabs.HOME -> HomeScreen(viewModel)
                }
            }
        }
    }

}

enum class NavigationTabs {
    HOME
}