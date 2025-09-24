/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.kmmreduxtemplate.repository.datasource

import com.turbosokol.kmmreduxtemplate.repository.data.CreateTaskRequest
import com.turbosokol.kmmreduxtemplate.repository.data.TaskDto
import com.turbosokol.kmmreduxtemplate.repository.data.UpdateTaskRequest

/**
 * Abstract interface for remote task data operations
 * Handles network communication with backend API
 */
interface RemoteTaskDataSource {
    
    /**
     * Fetches all tasks from remote server
     */
    suspend fun getTasks(): Result<List<TaskDto>>
    
    /**
     * Gets specific task by ID from remote
     */
    suspend fun getTaskById(id: Int): Result<TaskDto?>
    
    /**
     * Creates new task on remote server
     */
    suspend fun createTask(request: CreateTaskRequest): Result<TaskDto>
    
    /**
     * Updates existing task on remote server
     */
    suspend fun updateTask(request: UpdateTaskRequest): Result<TaskDto>
    
    /**
     * Deletes task from remote server
     */
    suspend fun deleteTask(id: Int): Result<Unit>
    
    /**
     * Syncs multiple tasks to remote (batch operation)
     */
    suspend fun syncTasks(tasks: List<TaskDto>): Result<List<TaskDto>>
}
