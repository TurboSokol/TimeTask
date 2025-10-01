/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.repository

import com.turbosokol.TimeTask.screensStates.TaskItem

/**
 * Repository interface for task management with cache-first strategy
 * Coordinates between local and remote data sources
 */
interface TaskRepository {
    
    /**
     * Gets all tasks with cache-first, fallback to network
     */
    suspend fun getTasks(forceRefresh: Boolean = false): Result<List<TaskItem>>
    
    suspend fun getTaskById(id: Int): Result<TaskItem?>
  
    suspend fun createTask(
        title: String, 
        description: String, 
        color: TaskItem.TaskColor
    ): Result<TaskItem>
    
    suspend fun updateTask(task: TaskItem): Result<TaskItem>
    
    suspend fun deleteTask(id: Int): Result<Unit>
    
    /**
     * Syncs local changes with remote server
     */
    suspend fun syncWithRemote(): Result<Unit>
    
    /**
     * Clears all local data (logout/reset)
     */
    suspend fun clearAllData(): Result<Unit>
}
