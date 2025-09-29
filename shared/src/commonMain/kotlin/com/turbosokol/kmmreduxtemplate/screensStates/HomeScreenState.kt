package com.turbosokol.kmmreduxtemplate.screensStates

import com.turbosokol.kmmreduxtemplate.core.redux.Action
import com.turbosokol.kmmreduxtemplate.core.redux.GeneralState

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

data class TaskItem(
    val id: Int,
    val title: String,
    val description: String,
    val isActive: Boolean,
    val timeSeconds: Long,
    val timeHours: Double,
    val color: TaskColor
) {
    enum class TaskColor {
        DEFAULT, YELLOW, PINK, BLUE, MINT
    }
}

data class HomeScreenState(
    val tasks: List<TaskItem>,
    val isLoading: Boolean = false,
    val error: String? = null,
    val firstLaunch: Boolean = true  // Track if we've completed the initial database load
) : GeneralState {
    companion object {
        fun getInitState(): HomeScreenState = HomeScreenState(
            tasks = emptyList(),  // Start with empty list, load from database
            isLoading = true,      // Show loading while database loads
            error = null,
            firstLaunch = true     // Initial load hasn't completed yet
        )
    }
}

sealed class HomeScreenAction : Action {
    data class ToggleTaskTimer(val taskId: Int) : HomeScreenAction()
    data class UpdateTaskTime(val taskId: Int, val timeSeconds: Long, val timeHours: Double) : HomeScreenAction()
    data class ResetTaskTime(val taskId: Int) : HomeScreenAction()
    data class CreateTask(val title: String, val description: String, val color: TaskItem.TaskColor) : HomeScreenAction()
    data class EditTask(val taskId: Int, val title: String, val description: String, val color: TaskItem.TaskColor, val timeSeconds: Long, val timeHours: Double) : HomeScreenAction()
    data class DeleteTask(val taskId: Int) : HomeScreenAction()
    
    // Repository-driven actions
    object LoadTasks : HomeScreenAction()
    object SyncTasks : HomeScreenAction()
    object SaveAppState : HomeScreenAction()  // For app onStop event
    data class TasksLoaded(val tasks: List<TaskItem>) : HomeScreenAction()
    data class TaskCreated(val task: TaskItem) : HomeScreenAction()
    data class TaskUpdated(val task: TaskItem) : HomeScreenAction()
    data class TaskDeleted(val taskId: Int) : HomeScreenAction()
    object AppStateSaved : HomeScreenAction()  // Confirmation of successful save
    data class TaskOperationFailed(val message: String) : HomeScreenAction()
}
