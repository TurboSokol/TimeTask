/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.service

import com.turbosokol.TimeTask.core.redux.Action
import com.turbosokol.TimeTask.core.redux.Store
import com.turbosokol.TimeTask.core.redux.app.AppState
import com.turbosokol.TimeTask.screensStates.HomeScreenAction
import com.turbosokol.TimeTask.screensStates.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Bridge between background services and Redux store
 * Allows services to dispatch actions to update the store state
 */
class ReduxServiceBridge private constructor() {
    
    private var store: Store<AppState, Action, *>? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main)
    
    companion object {
        @Volatile
        private var INSTANCE: ReduxServiceBridge? = null
        
        fun getInstance(): ReduxServiceBridge {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ReduxServiceBridge().also { INSTANCE = it }
            }
        }
    }
    
    /**
     * Initialize the bridge with the Redux store
     * Should be called from MainActivity or App class
     */
    fun initialize(store: Store<AppState, Action, *>) {
        this.store = store
    }
    
    /**
     * Dispatch a background timer update to the store
     * This allows the notification service to update task times
     */
    fun dispatchBackgroundTimerUpdate(taskId: Int, timeSeconds: Long, timeHours: Double) {
        store?.let { reduxStore ->
            serviceScope.launch {
                reduxStore.dispatch(
                    HomeScreenAction.BackgroundTimerUpdate(taskId, timeSeconds, timeHours)
                )
            }
        }
    }
    
    /**
     * Persist task update to database to prevent reset on app restart
     * This ensures background timer updates are saved to the database
     */
    fun persistTaskUpdate(task: TaskItem) {
        store?.let { reduxStore ->
            serviceScope.launch {
                // Dispatch UpdateTaskTime action which will be handled by middleware
                // and persisted to database
                reduxStore.dispatch(
                    HomeScreenAction.UpdateTaskTime(task.id, task.timeSeconds, task.timeHours)
                )
            }
        }
    }
    
    /**
     * Check if the bridge is initialized
     */
    fun isInitialized(): Boolean = store != null
}
