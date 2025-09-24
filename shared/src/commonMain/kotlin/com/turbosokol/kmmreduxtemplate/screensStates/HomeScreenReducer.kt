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
            is HomeScreenAction.ToggleTaskTimer -> {
                val updatedTasks = oldState.tasks.map { task ->
                    if (task.id == action.taskId) {
                        task.copy(isActive = !task.isActive)
                    } else {
                        task
                    }
                }
                oldState.copy(tasks = updatedTasks)
            }
            
            is HomeScreenAction.UpdateTaskTime -> {
                val updatedTasks = oldState.tasks.map { task ->
                    if (task.id == action.taskId) {
                        task.copy(
                            timeSeconds = action.timeSeconds,
                            timeHours = action.timeHours
                        )
                    } else {
                        task
                    }
                }
                oldState.copy(tasks = updatedTasks)
            }
            
            is HomeScreenAction.ResetTaskTime -> {
                val updatedTasks = oldState.tasks.map { task ->
                    if (task.id == action.taskId) {
                        task.copy(
                            timeSeconds = 0L,
                            timeHours = 0.0,
                            isActive = false  // Also stop the timer when resetting
                        )
                    } else {
                        task
                    }
                }
                oldState.copy(tasks = updatedTasks)
            }
            
            is HomeScreenAction.CreateTask -> {
                val newTaskId = (oldState.tasks.maxOfOrNull { it.id } ?: 0) + 1
                val newTask = TaskItem(
                    id = newTaskId,
                    title = action.title,
                    description = action.description,
                    isActive = false,
                    timeSeconds = 0L,
                    timeHours = 0.0,
                    color = action.color
                )
                oldState.copy(tasks = listOf(newTask) + oldState.tasks)
            }
            
            is HomeScreenAction.EditTask -> {
                val updatedTasks = oldState.tasks.map { task ->
                    if (task.id == action.taskId) {
                        task.copy(
                            title = action.title,
                            description = action.description,
                            color = action.color,
                            timeSeconds = action.timeSeconds,
                            timeHours = action.timeHours
                        )
                    } else {
                        task
                    }
                }
                oldState.copy(tasks = updatedTasks)
            }
            
            is HomeScreenAction.DeleteTask -> {
                val updatedTasks = oldState.tasks.filter { task ->
                    task.id != action.taskId
                }
                oldState.copy(tasks = updatedTasks)
            }

            else -> oldState
        }
    }
}