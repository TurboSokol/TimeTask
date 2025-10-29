/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.service

import android.content.Context
import com.turbosokol.TimeTask.core.redux.Action
import com.turbosokol.TimeTask.core.redux.Effect
import com.turbosokol.TimeTask.core.redux.Store
import com.turbosokol.TimeTask.core.redux.app.AppState
import com.turbosokol.TimeTask.database.DatabaseProvider
import com.turbosokol.TimeTask.repository.LocalTaskRepositoryImpl
import com.turbosokol.TimeTask.repository.datasource.SqlDelightLocalTaskDataSource
import com.turbosokol.TimeTask.screensStates.HomeScreenAction
import com.turbosokol.TimeTask.screensStates.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Service that handles timer updates from AlarmManager
 * Updates task times in database and triggers Redux state updates
 */
object TimerUpdateService {
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var reduxStore: Store<AppState, Action, Effect>? = null
    
    /**
     * Initialize the service with Redux store
     */
    fun initialize(store: Store<AppState, Action, Effect>) {
        reduxStore = store
    }
    
    /**
     * Update active tasks with elapsed time
     * Called by AlarmManager receiver
     */
    fun updateActiveTasks(context: Context, activeTaskIds: List<Int>, updateIntervalMs: Long) {
        serviceScope.launch {
            try {
                val updateIntervalSeconds = updateIntervalMs / 1000L
                
                // Create repository instance directly (without Koin)
                val database = DatabaseProvider.initializeDatabase(context)
                val localDataSource = SqlDelightLocalTaskDataSource(database)
                val taskRepository = LocalTaskRepositoryImpl(localDataSource, Dispatchers.IO)
                
                val updatedTasks = mutableListOf<TaskItem>()
                
                // Process tasks sequentially to ensure database operations complete
                for (taskId in activeTaskIds) {
                    try {
                        val taskResult = taskRepository.getTaskById(taskId)
                        val task = taskResult.getOrNull()
                        
                        if (task != null && task.isActive) {
                            val updatedTask = task.copy(
                                timeSeconds = task.timeSeconds + updateIntervalSeconds,
                                timeHours = (task.timeSeconds + updateIntervalSeconds) / 3600.0
                            )
                            
                            // Save updated task to database and wait for completion
                            val updateResult = taskRepository.updateTask(updatedTask)
                            updateResult.onSuccess {
                                updatedTasks.add(updatedTask)
                                println("TimerUpdateService: Updated task ${taskId} - ${updatedTask.timeSeconds}s (${String.format("%.1f", updatedTask.timeHours)}h)")
                            }.onFailure { error ->
                                println("TimerUpdateService: Failed to update task ${taskId}: ${error.message}")
                            }
                        } else {
                            println("TimerUpdateService: Task ${taskId} is no longer active, skipping")
                        }
                    } catch (e: Exception) {
                        println("TimerUpdateService: Exception processing task ${taskId}: ${e.message}")
                        e.printStackTrace()
                    }
                }
                
                if (updatedTasks.isNotEmpty()) {
                    println("TimerUpdateService: Successfully updated ${updatedTasks.size} tasks")
                    
                    // Dispatch Redux actions to update HomeState
                    reduxStore?.let { store ->
                        updatedTasks.forEach { updatedTask ->
                            store.dispatch(HomeScreenAction.TaskUpdated(updatedTask))
                        }
                        println("TimerUpdateService: Dispatched ${updatedTasks.size} TaskUpdated actions to Redux")
                    } ?: println("TimerUpdateService: Redux store not initialized, skipping state update")
                } else {
                    println("TimerUpdateService: No tasks were updated")
                }
            } catch (e: Exception) {
                println("TimerUpdateService: Error updating tasks: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
