/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.kmmreduxtemplate.repository.datasource

import com.turbosokol.kmmreduxtemplate.repository.data.TaskDto
import kotlinx.coroutines.flow.Flow

/**
 * Abstract interface for local task data persistence
 * Platform-specific implementations will handle actual storage (Room, SQLDelight, etc.)
 */
interface LocalTaskDataSource {
    
    /**
     * Observes all tasks as reactive flow
     */
    fun observeTasks(): Flow<List<TaskDto>>
    
    /**
     * Gets all tasks
     */
    suspend fun getAllTasks(): List<TaskDto>
    
    /**
     * Gets task by ID
     */
    suspend fun getTaskById(id: Int): TaskDto?
    
    /**
     * Inserts or updates a task
     */
    suspend fun upsertTask(task: TaskDto)
    
    /**
     * Inserts or updates multiple tasks
     */
    suspend fun upsertTasks(tasks: List<TaskDto>)
    
    /**
     * Deletes task by ID
     */
    suspend fun deleteTask(id: Int)
    
    /**
     * Deletes all tasks
     */
    suspend fun deleteAllTasks()
    
    /**
     * Gets next available ID for task creation
     */
    suspend fun getNextId(): Int
}
