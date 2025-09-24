/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.kmmreduxtemplate.repository.data

import com.turbosokol.kmmreduxtemplate.screensStates.TaskItem
import kotlinx.serialization.Serializable

/**
 * DTO for network serialization and local storage
 */
@Serializable
data class TaskDto(
    val id: Int,
    val title: String,
    val description: String,
    val isActive: Boolean,
    val timeSeconds: Long,
    val timeHours: Double,
    val color: String
)

/**
 * Maps domain TaskItem to DTO for persistence/network
 */
fun TaskItem.toDto(): TaskDto = TaskDto(
    id = id,
    title = title,
    description = description,
    isActive = isActive,
    timeSeconds = timeSeconds,
    timeHours = timeHours,
    color = color.name
)

/**
 * Maps DTO to domain TaskItem with safe color parsing
 */
fun TaskDto.toDomain(): TaskItem = TaskItem(
    id = id,
    title = title,
    description = description,
    isActive = isActive,
    timeSeconds = timeSeconds,
    timeHours = timeHours,
    color = try {
        TaskItem.TaskColor.valueOf(color)
    } catch (e: IllegalArgumentException) {
        TaskItem.TaskColor.DEFAULT
    }
)

/**
 * Network request/response DTOs
 */
@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String,
    val color: String
)

@Serializable
data class UpdateTaskRequest(
    val id: Int,
    val title: String,
    val description: String,
    val isActive: Boolean,
    val timeSeconds: Long,
    val timeHours: Double,
    val color: String
)

@Serializable
data class TaskListResponse(
    val tasks: List<TaskDto>
)
