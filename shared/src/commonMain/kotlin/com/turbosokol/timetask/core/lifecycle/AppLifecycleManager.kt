/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.core.lifecycle

import com.turbosokol.TimeTask.core.redux.Action
import com.turbosokol.TimeTask.core.redux.Store
import com.turbosokol.TimeTask.core.redux.app.AppState
import com.turbosokol.TimeTask.screensStates.HomeScreenAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Manages application lifecycle events and coordinates with Redux store
 * Handles app start, stop, pause, resume events across platforms
 */
class AppLifecycleManager(
    private val store: Store<AppState, Action, *>,
    private val lifecycleScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    
    /**
     * Called when app starts or resumes
     * Loads tasks from database
     */
    fun onAppStart() {
        lifecycleScope.launch {
            store.dispatch(HomeScreenAction.LoadTasks)
        }
    }
    
    /**
     * Called when app is about to stop or go to background
     * Saves current state to both local and remote storage
     */
    fun onAppStop() {
        lifecycleScope.launch {
            store.dispatch(HomeScreenAction.SaveAppState)
        }
    }
    
    /**
     * Called when app pauses (goes to background but might resume quickly)
     * Performs lightweight state saving
     */
    fun onAppPause() {
        lifecycleScope.launch {
            // Light sync - just ensure local database is up to date
            // Remote sync will happen on onAppStop if needed
            store.dispatch(HomeScreenAction.SaveAppState)
        }
    }
    
    /**
     * Called when app resumes from background
     * Refreshes data if needed
     */
    fun onAppResume() {
        lifecycleScope.launch {
            // Reload tasks to get any changes that might have occurred
            store.dispatch(HomeScreenAction.LoadTasks)
        }
    }
}






