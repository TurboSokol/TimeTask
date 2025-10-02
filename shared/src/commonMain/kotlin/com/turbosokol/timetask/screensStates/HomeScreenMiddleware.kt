package com.turbosokol.TimeTask.screensStates

import com.turbosokol.TimeTask.core.redux.Action
import com.turbosokol.TimeTask.core.redux.Effect
import com.turbosokol.TimeTask.core.redux.Middleware
import com.turbosokol.TimeTask.core.redux.app.AppState
import com.turbosokol.TimeTask.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

/**
 * Middleware handling side effects for HomeScreen task operations
 * Coordinates with TaskRepository for async operations
 */
class HomeScreenMiddleware(
    private val taskRepository: TaskRepository
) : Middleware<AppState> {
    
    override suspend fun execute(
        state: AppState,
        action: Action,
        sideEffect: MutableSharedFlow<Effect>
    ): Flow<Action> {
        return when (action) {
            is HomeScreenAction.CreateTask -> handleCreateTask(action)
            is HomeScreenAction.EditTask -> handleEditTask(action, state)
            is HomeScreenAction.DeleteTask -> handleDeleteTask(action)
            is HomeScreenAction.ToggleTaskTimer -> handleToggleTimer(action, state)
            is HomeScreenAction.UpdateTaskTime -> handleUpdateTaskTime(action, state)
            is HomeScreenAction.ResetTaskTime -> handleResetTaskTime(action, state)
            is HomeScreenAction.LoadTasks -> handleLoadTasks()
            is HomeScreenAction.SyncTasks -> handleSyncTasks()
            is HomeScreenAction.SaveAppState -> handleSaveAppState(state)
            else -> emptyFlow()
        }
    }
    
    private fun handleCreateTask(action: HomeScreenAction.CreateTask): Flow<Action> = flow {
        taskRepository.createTask(
            title = action.title,
            description = action.description,
            color = action.color
        ).onSuccess { createdTask ->
            emit(HomeScreenAction.TaskCreated(createdTask))
        }.onFailure { error ->
            emit(HomeScreenAction.TaskOperationFailed("Failed to create task: ${error.message}"))
        }
    }
    
    private fun handleEditTask(action: HomeScreenAction.EditTask, state: AppState): Flow<Action> = flow {
        val currentTask = state.getHomeScreenState().tasks.find { it.id == action.taskId }
        if (currentTask != null) {
            val updatedTask = currentTask.copy(
                title = action.title,
                description = action.description,
                color = action.color,
                timeSeconds = action.timeSeconds,
                timeHours = action.timeHours
            )
            
            taskRepository.updateTask(updatedTask).onSuccess { 
                emit(HomeScreenAction.TaskUpdated(updatedTask))
            }.onFailure { error ->
                emit(HomeScreenAction.TaskOperationFailed("Failed to update task: ${error.message}"))
            }
        }
    }
    
    private fun handleDeleteTask(action: HomeScreenAction.DeleteTask): Flow<Action> = flow {
        taskRepository.deleteTask(action.taskId).onSuccess {
            emit(HomeScreenAction.TaskDeleted(action.taskId))
        }.onFailure { error ->
            emit(HomeScreenAction.TaskOperationFailed("Failed to delete task: ${error.message}"))
        }
    }
    
    private fun handleLoadTasks(): Flow<Action> = flow {
        taskRepository.getTasks(forceRefresh = false).onSuccess { tasks ->
            emit(HomeScreenAction.TasksLoaded(tasks))
        }.onFailure { error ->
            emit(HomeScreenAction.TaskOperationFailed("Failed to load tasks: ${error.message}"))
        }
    }
    
    private fun handleSyncTasks(): Flow<Action> = flow {
        // For now, just reload tasks from local database
        // Remote sync will be added later when remote backend is ready
        taskRepository.getTasks(forceRefresh = false).onSuccess { tasks ->
            emit(HomeScreenAction.TasksLoaded(tasks))
            println("Tasks synced (local-only mode)")
        }.onFailure { error ->
            emit(HomeScreenAction.TaskOperationFailed("Failed to sync tasks: ${error.message}"))
        }
    }
    
    private fun handleToggleTimer(action: HomeScreenAction.ToggleTaskTimer, state: AppState): Flow<Action> = flow {
        val currentTask = state.getHomeScreenState().tasks.find { it.id == action.taskId }
        if (currentTask != null) {
            val updatedTask = currentTask.copy(isActive = !currentTask.isActive)
            println("HomeScreenMiddleware: ToggleTaskTimer - Task ${action.taskId} changing from ${currentTask.isActive} to ${updatedTask.isActive}")
            
            taskRepository.updateTask(updatedTask).onSuccess { 
                println("HomeScreenMiddleware: Task updated successfully, emitting TaskUpdated")
                emit(HomeScreenAction.TaskUpdated(updatedTask))
            }.onFailure { error ->
                println("HomeScreenMiddleware: Failed to update task: ${error.message}")
                emit(HomeScreenAction.TaskOperationFailed("Failed to toggle timer: ${error.message}"))
            }
        } else {
            println("HomeScreenMiddleware: Task ${action.taskId} not found")
        }
    }
    
    private fun handleUpdateTaskTime(action: HomeScreenAction.UpdateTaskTime, state: AppState): Flow<Action> = flow {
        val currentTask = state.getHomeScreenState().tasks.find { it.id == action.taskId }
        if (currentTask != null) {
            val updatedTask = currentTask.copy(
                timeSeconds = action.timeSeconds,
                timeHours = action.timeHours
            )
            
            taskRepository.updateTask(updatedTask).onSuccess { 
                emit(HomeScreenAction.TaskUpdated(updatedTask))
            }.onFailure { error ->
                emit(HomeScreenAction.TaskOperationFailed("Failed to update task time: ${error.message}"))
            }
        }
    }
    
    private fun handleResetTaskTime(action: HomeScreenAction.ResetTaskTime, state: AppState): Flow<Action> = flow {
        val currentTask = state.getHomeScreenState().tasks.find { it.id == action.taskId }
        if (currentTask != null) {
            val updatedTask = currentTask.copy(
                timeSeconds = 0L,
                timeHours = 0.0,
                isActive = false  // Also stop the timer when resetting
            )
            
            taskRepository.updateTask(updatedTask).onSuccess { 
                emit(HomeScreenAction.TaskUpdated(updatedTask))
            }.onFailure { error ->
                emit(HomeScreenAction.TaskOperationFailed("Failed to reset task time: ${error.message}"))
            }
        }
    }
    
    private fun handleSaveAppState(state: AppState): Flow<Action> = flow {
        // Save app state - for now just local persistence (all updates are already saved)
        try {
            // All task updates are already saved to local database in real-time
            // Remote sync will be added later when remote backend is ready
            println("App state saved successfully (local-only mode)")
            emit(HomeScreenAction.AppStateSaved)
        } catch (e: Exception) {
            emit(HomeScreenAction.TaskOperationFailed("Failed to save app state: ${e.message}"))
        }
    }
}
