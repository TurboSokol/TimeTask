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
    val tasks: List<TaskItem>
) : GeneralState {
    companion object {
        fun getInitState(): HomeScreenState = HomeScreenState(
            tasks = listOf(
                TaskItem(1, "Design UI", "Create mockups for the mobile app", false, 0L, 0.0, TaskItem.TaskColor.DEFAULT),
                TaskItem(2, "Backend API", "Implement REST endpoints", false, 3600L, 1.0, TaskItem.TaskColor.BLUE),
                TaskItem(3, "Testing", "Write unit tests for core functionality", false, 7200L, 2.0, TaskItem.TaskColor.MINT),
                TaskItem(4, "Documentation", "Update API documentation", false, 1800L, 0.5, TaskItem.TaskColor.YELLOW),
                TaskItem(5, "Code Review", "Review pull requests from team", false, 0L, 0.0, TaskItem.TaskColor.PINK)
            )
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
}
