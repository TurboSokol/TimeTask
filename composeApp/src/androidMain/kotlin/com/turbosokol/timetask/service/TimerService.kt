/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.service

import android.content.Context
import com.turbosokol.TimeTask.core.redux.Action
import com.turbosokol.TimeTask.core.redux.Effect
import com.turbosokol.TimeTask.core.redux.Middleware
import com.turbosokol.TimeTask.core.redux.app.AppState
import com.turbosokol.TimeTask.screensStates.HomeScreenAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock

/**
 * Timer service middleware that handles timer-related Redux actions.
 * Manages foreground service for timer processing.
 * All timer updates are performed by SimpleTaskNotificationService.
 */
class TimerService(
    private val context: Context
) : Middleware<AppState> {
    
    override suspend fun execute(
        state: AppState,
        action: Action,
        sideEffect: MutableSharedFlow<Effect>
    ): Flow<Action> {
        return when (action) {
            is HomeScreenAction.StartTaskTimer -> handleStartTimer(action, state)
            is HomeScreenAction.PauseTaskTimer -> handlePauseTimer(action, state)
            is HomeScreenAction.UpdateTaskTime -> handleUpdateTaskTime(action, state)
            is HomeScreenAction.ResetTaskTime -> handleResetTaskTime(action, state)
            else -> emptyFlow()
        }
    }
    
    private fun handleStartTimer(action: HomeScreenAction.StartTaskTimer, state: AppState): Flow<Action> = flow {
        val currentTask = state.getHomeScreenState().tasks.find { it.id == action.taskId }
        if (currentTask != null && !currentTask.isActive) {
            // Set startTimeStamp to current time in seconds
            val startTimeStamp = Clock.System.now().epochSeconds
            val updatedTask = currentTask.copy(
                isActive = true,
                startTimeStamp = startTimeStamp
            )
            emit(HomeScreenAction.TaskUpdated(updatedTask))
            
            // Get all active tasks (including the one we just started)
            val activeTasks = state.getHomeScreenState().tasks
                .map { if (it.id == action.taskId) updatedTask else it }
                .filter { it.isActive }
            
            // Start/update foreground service with all active tasks
            SimpleTaskNotificationService.startService(context, activeTasks)
            
            println("TimerService: Started timer for task ${action.taskId} - ${activeTasks.size} active tasks")
        }
    }
    
    private fun handlePauseTimer(action: HomeScreenAction.PauseTaskTimer, state: AppState): Flow<Action> = flow {
        val currentTask = state.getHomeScreenState().tasks.find { it.id == action.taskId }
        if (currentTask != null && currentTask.isActive) {
            val updatedTask = currentTask.copy(
                isActive = false,
                startTimeStamp = 0L  // Reset start timestamp when pausing
            )
            emit(HomeScreenAction.TaskUpdated(updatedTask))
            
            // Get remaining active tasks
            val activeTasks = state.getHomeScreenState().tasks
                .map { if (it.id == action.taskId) updatedTask else it }
                .filter { it.isActive }
            
            if (activeTasks.isNotEmpty()) {
                // Update foreground service with remaining active tasks
                SimpleTaskNotificationService.updateTasks(context, activeTasks)
            } else {
                // No active tasks remaining, stop service
                SimpleTaskNotificationService.stopService(context)
            }
            
            println("TimerService: Paused timer for task ${action.taskId} - ${activeTasks.size} active tasks remaining")
        }
    }
    
    
    private fun handleUpdateTaskTime(action: HomeScreenAction.UpdateTaskTime, state: AppState): Flow<Action> = flow {
        val currentTask = state.getHomeScreenState().tasks.find { it.id == action.taskId }
        if (currentTask != null) {
            val updatedTask = currentTask.copy(
                timeSeconds = action.timeSeconds,
                timeHours = action.timeHours
            )
            emit(HomeScreenAction.TaskUpdated(updatedTask))
            println("TimerService: Updated task ${action.taskId} time to ${action.timeSeconds}s")
        }
    }
    
    private fun handleResetTaskTime(action: HomeScreenAction.ResetTaskTime, state: AppState): Flow<Action> = flow {
        val currentTask = state.getHomeScreenState().tasks.find { it.id == action.taskId }
        if (currentTask != null) {
            val updatedTask = currentTask.copy(
                timeSeconds = 0L,
                timeHours = 0.0,
                startTimeStamp = 0L,
                isActive = false
            )
            emit(HomeScreenAction.TaskUpdated(updatedTask))
            
            // Get remaining active tasks
            val activeTasks = state.getHomeScreenState().tasks
                .map { if (it.id == action.taskId) updatedTask else it }
                .filter { it.isActive }
            
            if (activeTasks.isNotEmpty()) {
                // Update foreground service with remaining active tasks
                SimpleTaskNotificationService.updateTasks(context, activeTasks)
            } else {
                // No active tasks remaining, stop service
                SimpleTaskNotificationService.stopService(context)
            }
            
            println("TimerService: Reset timer for task ${action.taskId} - ${activeTasks.size} active tasks remaining")
        }
    }
}
