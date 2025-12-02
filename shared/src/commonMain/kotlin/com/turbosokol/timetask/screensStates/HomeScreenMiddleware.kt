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
import kotlinx.datetime.Clock

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
            is HomeScreenAction.StartTaskTimer -> handleStartTimer(action, state)
            is HomeScreenAction.PauseTaskTimer -> handlePauseTimer(action, state)
            is HomeScreenAction.UpdateTaskTime -> handleUpdateTaskTime(action, state)
            is HomeScreenAction.ResetTaskTime -> handleResetTaskTime(action, state)
            is HomeScreenAction.LoadTasks -> handleLoadTasks()
            is HomeScreenAction.SyncTasks -> handleSyncTasks()
            is HomeScreenAction.SaveAppState -> handleSaveAppState(state)
            is HomeScreenAction.BackgroundTimerUpdate -> handleBackgroundTimerUpdate(action, state)
            else -> emptyFlow()
        }
    }
    
    private fun handleCreateTask(action: HomeScreenAction.CreateTask): Flow<Action> = flow {
        taskRepository.createTask(
            title = action.title,
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
    
    private fun handleStartTimer(action: HomeScreenAction.StartTaskTimer, state: AppState): Flow<Action> = flow {
        val currentTask = state.getHomeScreenState().tasks.find { it.id == action.taskId }
        if (currentTask != null && !currentTask.isActive) {
            // Set startTimeStamp to current time in seconds (equivalent to System.currentTimeMillis() / 1000)
            val startTimeStamp = Clock.System.now().epochSeconds
            val updatedTask = currentTask.copy(
                isActive = true,
                startTimeStamp = startTimeStamp
            )
            println("HomeScreenMiddleware: StartTaskTimer - Task ${action.taskId} starting at timestamp $startTimeStamp")
            
            taskRepository.updateTask(updatedTask).onSuccess { 
                emit(HomeScreenAction.TaskUpdated(updatedTask))
            }.onFailure { error ->
                emit(HomeScreenAction.TaskOperationFailed("Failed to start timer: ${error.message}"))
            }
        }
    }
    
    private fun handlePauseTimer(action: HomeScreenAction.PauseTaskTimer, state: AppState): Flow<Action> = flow {
        val currentTask = state.getHomeScreenState().tasks.find { it.id == action.taskId }
        if (currentTask != null && currentTask.isActive) {
            val updatedTask = currentTask.copy(
                isActive = false,
                startTimeStamp = 0L  // Reset start timestamp when pausing
            )
            println("HomeScreenMiddleware: PauseTaskTimer - Task ${action.taskId} pausing")
            
            taskRepository.updateTask(updatedTask).onSuccess { 
                emit(HomeScreenAction.TaskUpdated(updatedTask))
            }.onFailure { error ->
                emit(HomeScreenAction.TaskOperationFailed("Failed to pause timer: ${error.message}"))
            }
        }
    }
    
    
    private fun handleUpdateTaskTime(action: HomeScreenAction.UpdateTaskTime, state: AppState): Flow<Action> = flow {
        val currentTask = state.getHomeScreenState().tasks.find { it.id == action.taskId }
        if (currentTask != null) {
            val updatedTask = currentTask.copy(
                timeSeconds = action.timeSeconds,
                timeHours = action.timeHours
                // DON'T update startTimeStamp here - it should only be set when starting/pausing/resetting
            )
            
            // For real-time UI updates, we update the state immediately
            // and also persist to database (but don't wait for database response)
            emit(HomeScreenAction.TaskUpdated(updatedTask))
            
            // Persist to database in background (non-blocking)
            taskRepository.updateTask(updatedTask).onFailure { error ->
                println("HomeScreenMiddleware: Failed to persist timer update for task ${action.taskId}: ${error.message}")
                // Note: We don't emit error here to avoid disrupting the timer flow
                // The UI state is already updated, database sync will happen via AlarmManager
            }
        }
    }
    
    private fun handleResetTaskTime(action: HomeScreenAction.ResetTaskTime, state: AppState): Flow<Action> = flow {
        val currentTask = state.getHomeScreenState().tasks.find { it.id == action.taskId }
        if (currentTask != null) {
            val updatedTask = currentTask.copy(
                timeSeconds = 0L,
                timeHours = 0.0,
                startTimeStamp = 0L,  // Reset start timestamp as well
                isActive = false  // Also stop the timer when resetting
            )
            
            taskRepository.updateTask(updatedTask).onSuccess { 
                emit(HomeScreenAction.TaskUpdated(updatedTask))
            }.onFailure { error ->
                emit(HomeScreenAction.TaskOperationFailed("Failed to reset task time: ${error.message}"))
            }
        }
    }
    
    private fun handleSaveAppState(@Suppress("UNUSED_PARAMETER") state: AppState): Flow<Action> = flow {
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
    
    private fun handleBackgroundTimerUpdate(action: HomeScreenAction.BackgroundTimerUpdate, @Suppress("UNUSED_PARAMETER") state: AppState): Flow<Action> = flow {
        // Background timer updates are handled directly in the reducer for immediate UI updates
        // This method exists for consistency but doesn't need to do anything
        // The reducer already handles BackgroundTimerUpdate actions
    }
}
