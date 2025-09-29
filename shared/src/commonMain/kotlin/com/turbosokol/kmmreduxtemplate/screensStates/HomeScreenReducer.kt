package com.turbosokol.kmmreduxtemplate.screensStates

import com.turbosokol.kmmreduxtemplate.core.redux.Action
import com.turbosokol.kmmreduxtemplate.core.redux.Reducer

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

class HomeScreenReducer : Reducer<HomeScreenState> {
    override fun reduce(oldState: HomeScreenState, action: Action): HomeScreenState {
        return when (action) {
            // UI-only actions that don't require persistence are handled by middleware
            is HomeScreenAction.ToggleTaskTimer,
            is HomeScreenAction.UpdateTaskTime,
            is HomeScreenAction.ResetTaskTime,
            is HomeScreenAction.CreateTask,
            is HomeScreenAction.EditTask,
            is HomeScreenAction.DeleteTask,
            is HomeScreenAction.SaveAppState -> {
                // These actions are handled by middleware and result in repository updates
                // State updates come through TaskUpdated/TaskCreated/TaskDeleted actions
                oldState
            }
            
            // Repository-driven actions
            is HomeScreenAction.LoadTasks -> {
                oldState.copy(isLoading = true, error = null)
            }
            
            is HomeScreenAction.TasksLoaded -> {
                oldState.copy(
                    tasks = action.tasks,
                    isLoading = false,
                    error = null,
                    firstLaunch = false  // Mark that initial database load is complete
                )
            }
            
            is HomeScreenAction.TaskCreated -> {
                // Replace local optimistic task with server response
                val existingTask = oldState.tasks.find { it.id == action.task.id }
                if (existingTask != null) {
                    // Update existing task with server data
                    val updatedTasks = oldState.tasks.map { task ->
                        if (task.id == action.task.id) action.task else task
                    }
                    oldState.copy(tasks = updatedTasks, firstLaunch = false)
                } else {
                    // Add new task
                    oldState.copy(tasks = listOf(action.task) + oldState.tasks, firstLaunch = false)
                }
            }
            
            is HomeScreenAction.TaskUpdated -> {
                val updatedTasks = oldState.tasks.map { task ->
                    if (task.id == action.task.id) action.task else task
                }
                oldState.copy(tasks = updatedTasks, firstLaunch = false)
            }
            
            is HomeScreenAction.TaskDeleted -> {
                val updatedTasks = oldState.tasks.filter { task ->
                    task.id != action.taskId
                }
                oldState.copy(tasks = updatedTasks, firstLaunch = false)
            }
            
            is HomeScreenAction.AppStateSaved -> {
                // App state has been successfully saved to local and remote
                println("App state saved successfully")
                oldState
            }
            
            is HomeScreenAction.TaskOperationFailed -> {
                println("Task operation failed: ${action.message}")
                oldState.copy(
                    isLoading = false,
                    error = action.message,
                    firstLaunch = false  // Mark initial load as complete even if it failed
                )
            }

            else -> oldState
        }
    }
}