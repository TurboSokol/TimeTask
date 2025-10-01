/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.repository.datasource

import com.turbosokol.TimeTask.repository.data.TaskDto

/**
 * Abstract interface for local task data persistence
 * Platform-specific implementations will handle actual storage (Room, SQLDelight, etc.)
 */
interface LocalTaskDataSource {

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
}
