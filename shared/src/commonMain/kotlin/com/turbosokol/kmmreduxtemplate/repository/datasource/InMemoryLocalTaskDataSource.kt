/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.kmmreduxtemplate.repository.datasource

import com.turbosokol.kmmreduxtemplate.repository.data.TaskDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * In-memory implementation of LocalTaskDataSource for MVP/testing
 * Replace with Room/SQLDelight for production persistence
 */
class InMemoryLocalTaskDataSource : LocalTaskDataSource {
    
    private val mutex = Mutex()
    private val tasks = MutableStateFlow<Map<Int, TaskDto>>(emptyMap())
    private var nextId = 1

    override fun observeTasks(): Flow<List<TaskDto>> =
        tasks.asStateFlow().map { it.values.toList() }

    override suspend fun getAllTasks(): List<TaskDto> = mutex.withLock {
        tasks.value.values.toList()
    }

    override suspend fun getTaskById(id: Int): TaskDto? = mutex.withLock {
        tasks.value[id]
    }

    override suspend fun upsertTask(task: TaskDto) = mutex.withLock {
        tasks.value = tasks.value + (task.id to task)
        // Update nextId if needed
        if (task.id >= nextId) {
            nextId = task.id + 1
        }
    }

    override suspend fun upsertTasks(tasks: List<TaskDto>) = mutex.withLock {
        val taskMap = tasks.associateBy { it.id }
        this.tasks.value = this.tasks.value + taskMap
        // Update nextId to highest + 1
        val maxId = tasks.maxOfOrNull { it.id } ?: 0
        if (maxId >= nextId) {
            nextId = maxId + 1
        }
    }

    override suspend fun deleteTask(id: Int) = mutex.withLock {
        tasks.value = tasks.value - id
    }

    override suspend fun deleteAllTasks() = mutex.withLock {
        tasks.value = emptyMap()
        nextId = 1
    }

    override suspend fun getNextId(): Int = mutex.withLock {
        nextId++
    }
}
