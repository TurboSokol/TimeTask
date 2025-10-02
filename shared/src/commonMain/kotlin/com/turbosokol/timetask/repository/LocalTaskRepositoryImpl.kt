/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.repository

import com.turbosokol.TimeTask.repository.data.toDomain
import com.turbosokol.TimeTask.repository.data.toDto
import com.turbosokol.TimeTask.repository.datasource.LocalTaskDataSource
import com.turbosokol.TimeTask.screensStates.TaskItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Local-only implementation of TaskRepository 
 * Only uses local database, no remote sync for now
 * 
 * Strategy:
 * - All operations work directly with local database
 * - No network calls or remote sync
 * - Fast and reliable offline-first experience
 */
class LocalTaskRepositoryImpl(
    private val localDataSource: LocalTaskDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) : TaskRepository {

    override suspend fun getTasks(forceRefresh: Boolean): Result<List<TaskItem>> = withContext(ioDispatcher) {
        runCatching {
            // Simply get all tasks from local database
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
            println("LocalTaskRepository: Creating task - title: '$title', description: '$description'")
            
            val newTask = TaskItem(
                title = title,
                description = description,
                isActive = false,
                timeSeconds = 0L,
                timeHours = 0.0,
                color = color
            )
            
            println("LocalTaskRepository: Task created with ID: ${newTask.id}")
            
            // Save to local database and get the task with correct ID
            val savedTaskDto = localDataSource.upsertTask(newTask.toDto())
            val savedTask = savedTaskDto.toDomain()
            
            println("LocalTaskRepository: Task saved to database with ID: ${savedTask.id}")
            savedTask
        }
    }

    override suspend fun updateTask(task: TaskItem): Result<TaskItem> = withContext(ioDispatcher) {
        runCatching {
            // Update in local database
            localDataSource.upsertTask(task.toDto())
            task
        }
    }

    override suspend fun deleteTask(id: Int): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            localDataSource.deleteTask(id)
            Unit
        }
    }

    override suspend fun syncWithRemote(): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            // No-op for now - will implement remote sync later
            println("LocalTaskRepository: syncWithRemote() - not implemented yet (local-only mode)")
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






