/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.repository

import com.turbosokol.TimeTask.repository.data.UpdateTaskRequest
import com.turbosokol.TimeTask.repository.data.toDomain
import com.turbosokol.TimeTask.repository.data.toDto
import com.turbosokol.TimeTask.repository.datasource.LocalTaskDataSource
import com.turbosokol.TimeTask.repository.datasource.RemoteTaskDataSource
import com.turbosokol.TimeTask.screensStates.TaskItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Implementation of TaskRepository following cache-first strategy
 * 
 * Strategy:
 * - All reads prioritize local cache, with optional remote fallback
 * - All writes update local immediately, then sync to remote
 * - Network failures don't block local operations
 * - Provides offline-first experience
 */
class TaskRepositoryImpl(
    private val localDataSource: LocalTaskDataSource,
    private val remoteDataSource: RemoteTaskDataSource,
    private val ioDispatcher: CoroutineDispatcher
) : TaskRepository {

    override suspend fun getTasks(forceRefresh: Boolean): Result<List<TaskItem>> = withContext(ioDispatcher) {
        runCatching {
            // If force refresh or local is empty, try remote first
            if (forceRefresh || localDataSource.getAllTasks().isEmpty()) {
                val remoteResult = remoteDataSource.getTasks()
                remoteResult.onSuccess { remoteTasks ->
                    // Cache remote data locally
                    localDataSource.upsertTasks(remoteTasks)
                }
            }
            
            // Always return from local cache (single source of truth)
            localDataSource.getAllTasks().map { it.toDomain() }
        }
    }

    override suspend fun getTaskById(id: Int): Result<TaskItem?> = withContext(ioDispatcher) {
        runCatching {
            localDataSource.getTaskById(id)?.toDomain()
        }
    }

    override suspend fun createTask(
        title: String,
        color: TaskItem.TaskColor
    ): Result<TaskItem> = withContext(ioDispatcher) {
        runCatching {
            val newTask = TaskItem(
                title = title,
                isActive = false,
                timeSeconds = 0L,
                timeHours = 0.0,
                color = color
            )
            
            // Save locally first (optimistic UI) and get the task with correct ID
            val savedTaskDto = localDataSource.upsertTask(newTask.toDto())
            val savedTask = savedTaskDto.toDomain()
            
            // Note: Remote sync is disabled for now - tasks are stored locally only
            // When remote sync is implemented, we'll need to handle ID mapping between local and remote
            
            savedTask
        }
    }

    override suspend fun updateTask(task: TaskItem): Result<TaskItem> = withContext(ioDispatcher) {
        runCatching {
            // Update locally first
            localDataSource.upsertTask(task.toDto())
            
            // Sync to remote
            try {
                val updateRequest = UpdateTaskRequest(
                    id = task.id,
                    title = task.title,
                    isActive = task.isActive,
                    timeSeconds = task.timeSeconds,
                    timeHours = task.timeHours,
                    color = task.color.name
                )
                remoteDataSource.updateTask(updateRequest)
            } catch (e: Exception) {
                // Network error - changes stay local until next sync
                println("TaskRepository: Failed to sync task update to remote - ${e.message}")
            }
            
            task
        }
    }

    override suspend fun deleteTask(id: Int): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            // Delete locally first
            localDataSource.deleteTask(id)
            
            // Sync deletion to remote
            try {
                remoteDataSource.deleteTask(id)
            } catch (e: Exception) {
                // Network error - deletion stays local until next sync
                println("TaskRepository: Failed to sync task deletion to remote - ${e.message}")
            }
            Unit
        }
    }

    override suspend fun syncWithRemote(): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            // Get all local tasks
            val localTasks = localDataSource.getAllTasks()
            
            // Sync to remote
            val syncResult = remoteDataSource.syncTasks(localTasks)
            syncResult.onSuccess { syncedTasks ->
                // Update local with any server changes
                localDataSource.upsertTasks(syncedTasks)
            }
            
            // Also fetch any new tasks from remote
            val remoteResult = remoteDataSource.getTasks()
            remoteResult.onSuccess { remoteTasks ->
                localDataSource.upsertTasks(remoteTasks)
            }
            Unit
        }
    }

    override suspend fun clearAllData(): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            localDataSource.deleteAllTasks()
            Unit
        }
    }
}
