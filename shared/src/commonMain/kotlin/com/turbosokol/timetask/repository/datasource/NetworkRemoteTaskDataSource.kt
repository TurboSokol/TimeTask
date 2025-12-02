/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.repository.datasource

import com.turbosokol.TimeTask.repository.data.CreateTaskRequest
import com.turbosokol.TimeTask.repository.data.TaskDto
import com.turbosokol.TimeTask.repository.data.UpdateTaskRequest

/**
 * Network implementation of RemoteTaskDataSource using existing API infrastructure
 * Currently returns mock data - integrate with actual backend when available
 */
class NetworkRemoteTaskDataSource : RemoteTaskDataSource {

    override suspend fun getTasks(): Result<List<TaskDto>> = runCatching {
        // TODO: Integrate with actual backend API
        // For now, return empty list to allow local-first operation
        emptyList()
    }

    override suspend fun getTaskById(id: Int): Result<TaskDto?> = runCatching {
        // TODO: Implement actual network call
        null
    }

    override suspend fun createTask(request: CreateTaskRequest): Result<TaskDto> = runCatching {
        // TODO: Implement actual network call
        // Return mock task for now
        TaskDto(
            id = (1..1000).random(), // Server would assign actual ID
            title = request.title,
            isActive = false,
            startTimeStamp = 0L,
            timeSeconds = 0L,
            timeHours = 0.0,
            color = request.color
        )
    }

    override suspend fun updateTask(request: UpdateTaskRequest): Result<TaskDto> = runCatching {
        // TODO: Implement actual network call
        TaskDto(
            id = request.id,
            title = request.title,
            isActive = request.isActive,
            startTimeStamp = request.startTimeStamp,
            timeSeconds = request.timeSeconds,
            timeHours = request.timeHours,
            color = request.color
        )
    }

    override suspend fun deleteTask(id: Int): Result<Unit> = runCatching {
        // TODO: Implement actual network call
        Unit
    }

    override suspend fun syncTasks(tasks: List<TaskDto>): Result<List<TaskDto>> = runCatching {
        // TODO: Implement batch sync with server
        tasks
    }
}
