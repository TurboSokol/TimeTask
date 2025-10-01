/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.repository.datasource

import com.turbosokol.TimeTask.repository.data.TaskDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * In-memory implementation of LocalTaskDataSource for platforms without SQLDelight support
 * Provides temporary storage that persists only during app session
 * Used for WASM/JS platform where SQLDelight is not yet available
 */
class InMemoryLocalTaskDataSource : LocalTaskDataSource {
    
    private val tasks = mutableMapOf<Int, TaskDto>()
    private val tasksFlow = MutableStateFlow<List<TaskDto>>(emptyList())
    private val mutex = Mutex()
    private var nextId = 1
    
    override suspend fun getAllTasks(): List<TaskDto> = mutex.withLock {
        tasks.values.toList()
    }
    
    override suspend fun getTaskById(id: Int): TaskDto? = mutex.withLock {
        tasks[id]
    }
    
    override suspend fun upsertTask(task: TaskDto) = mutex.withLock {
        val taskToSave = if (task.id == 0) {
            // Generate new ID for in-memory storage
            task.copy(id = nextId++)
        } else {
            task
        }
        tasks[taskToSave.id] = taskToSave
        updateFlow()
    }
    
    override suspend fun upsertTasks(tasks: List<TaskDto>) = mutex.withLock {
        tasks.forEach { task ->
            val taskToSave = if (task.id == 0) {
                // Generate new ID for in-memory storage
                task.copy(id = nextId++)
            } else {
                task
            }
            this.tasks[taskToSave.id] = taskToSave
        }
        updateFlow()
    }
    
    override suspend fun deleteTask(id: Int) = mutex.withLock {
        tasks.remove(id)
        updateFlow()
    }
    
    override suspend fun deleteAllTasks() = mutex.withLock {
        tasks.clear()
        nextId = 1
        updateFlow()
    }
    
    private fun updateFlow() {
        tasksFlow.value = tasks.values.toList()
    }
}


