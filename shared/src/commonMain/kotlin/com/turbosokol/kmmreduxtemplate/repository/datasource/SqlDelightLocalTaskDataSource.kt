/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.kmmreduxtemplate.repository.datasource

import com.turbosokol.kmmreduxtemplate.database.TaskDatabase
import com.turbosokol.kmmreduxtemplate.repository.data.TaskDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * SQLDelight implementation of LocalTaskDataSource for multiplatform support
 * Provides offline-first data persistence using SQLDelight database
 */
class SqlDelightLocalTaskDataSource(
    private val database: TaskDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) : LocalTaskDataSource {
    
    override fun observeTasks(): Flow<List<TaskDto>> = flow {
        emit(getAllTasks())
    }
    
    override suspend fun getAllTasks(): List<TaskDto> = withContext(ioDispatcher) {
        database.taskDatabaseQueries.selectAll().executeAsList().map { entity ->
            TaskDto(
                id = entity.id.toInt(),
                title = entity.title,
                description = entity.description,
                isActive = entity.isActive == 1L,
                timeSeconds = entity.timeSeconds,
                timeHours = entity.timeHours,
                color = entity.color
            )
        }
    }
    
    override suspend fun getTaskById(id: Int): TaskDto? = withContext(ioDispatcher) {
        database.taskDatabaseQueries.selectById(id.toLong()).executeAsOneOrNull()?.let { entity ->
            TaskDto(
                id = entity.id.toInt(),
                title = entity.title,
                description = entity.description,
                isActive = entity.isActive == 1L,
                timeSeconds = entity.timeSeconds,
                timeHours = entity.timeHours,
                color = entity.color
            )
        }
    }
    
    override suspend fun upsertTask(task: TaskDto) = withContext(ioDispatcher) {
        database.taskDatabaseQueries.insertOrReplace(
            id = task.id.toLong(),
            title = task.title,
            description = task.description,
            isActive = if (task.isActive) 1L else 0L,
            timeSeconds = task.timeSeconds,
            timeHours = task.timeHours,
            color = task.color
        )
    }
    
    override suspend fun upsertTasks(tasks: List<TaskDto>) = withContext(ioDispatcher) {
        database.transaction {
            tasks.forEach { task ->
                database.taskDatabaseQueries.insertOrReplace(
                    id = task.id.toLong(),
                    title = task.title,
                    description = task.description,
                    isActive = if (task.isActive) 1L else 0L,
                    timeSeconds = task.timeSeconds,
                    timeHours = task.timeHours,
                    color = task.color
                )
            }
        }
    }
    
    override suspend fun deleteTask(id: Int) = withContext(ioDispatcher) {
        database.taskDatabaseQueries.deleteById(id.toLong())
    }
    
    override suspend fun deleteAllTasks() = withContext(ioDispatcher) {
        database.taskDatabaseQueries.deleteAll()
    }
    
    override suspend fun getNextId(): Int = withContext(ioDispatcher) {
        database.taskDatabaseQueries.getNextId().executeAsOne().toInt()
    }
}
