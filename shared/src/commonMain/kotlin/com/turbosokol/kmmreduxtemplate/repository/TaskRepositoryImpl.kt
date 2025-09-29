/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.kmmreduxtemplate.repository

import com.turbosokol.kmmreduxtemplate.repository.data.CreateTaskRequest
import com.turbosokol.kmmreduxtemplate.repository.data.UpdateTaskRequest
import com.turbosokol.kmmreduxtemplate.repository.data.toDomain
import com.turbosokol.kmmreduxtemplate.repository.data.toDto
import com.turbosokol.kmmreduxtemplate.repository.datasource.LocalTaskDataSource
import com.turbosokol.kmmreduxtemplate.repository.datasource.RemoteTaskDataSource
import com.turbosokol.kmmreduxtemplate.screensStates.TaskItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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
        description: String,
        color: TaskItem.TaskColor
    ): Result<TaskItem> = withContext(ioDispatcher) {
        runCatching {
            // Generate local ID and create task
            val localId = localDataSource.getNextId()
            val newTask = TaskItem(
                id = localId,
                title = title,
                description = description,
                isActive = false,
                timeSeconds = 0L,
                timeHours = 0.0,
                color = color
            )
            
            // Save locally first (optimistic UI)
            localDataSource.upsertTask(newTask.toDto())
            
            // Sync to remote in background (fire-and-forget for now)
            try {
                val createRequest = CreateTaskRequest(title, description, color.name)
                remoteDataSource.createTask(createRequest).onSuccess { remoteTask ->
                    // Update local with server-assigned ID if different
                    if (remoteTask.id != localId) {
                        localDataSource.deleteTask(localId)
                        localDataSource.upsertTask(remoteTask)
                    }
                }
            } catch (e: Exception) {
                // Network error - task stays local until next sync
                println("TaskRepository: Failed to sync new task to remote - ${e.message}")
            }
            
            newTask
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
                    description = task.description,
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
