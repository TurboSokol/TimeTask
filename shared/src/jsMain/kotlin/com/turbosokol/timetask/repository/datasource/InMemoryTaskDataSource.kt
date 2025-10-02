/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.repository.datasource

import com.turbosokol.TimeTask.repository.data.TaskDto
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Simple in-memory implementation of LocalTaskDataSource for JS
 */
class InMemoryTaskDataSource : LocalTaskDataSource {
    
    private val tasks = mutableListOf<TaskDto>()
    private val mutex = Mutex()
    private var nextId = 1
    
    override suspend fun getAllTasks(): List<TaskDto> = mutex.withLock {
        tasks.toList()
    }
    
    override suspend fun getTaskById(id: Int): TaskDto? = mutex.withLock {
        tasks.find { it.id == id }
    }
    
    override suspend fun upsertTask(task: TaskDto) {
        mutex.withLock {
            if (task.id > 0) {
                // Update existing task
                val index = tasks.indexOfFirst { it.id == task.id }
                if (index >= 0) {
                    tasks[index] = task
                }
            } else {
                // Insert new task
                val newTask = task.copy(id = nextId++)
                tasks.add(newTask)
            }
        }
    }
    
    override suspend fun upsertTasks(tasks: List<TaskDto>) {
        mutex.withLock {
            tasks.forEach { task ->
                if (task.id > 0) {
                    // Update existing task
                    val index = this.tasks.indexOfFirst { it.id == task.id }
                    if (index >= 0) {
                        this.tasks[index] = task
                    }
                } else {
                    // Insert new task
                    val newTask = task.copy(id = nextId++)
                    this.tasks.add(newTask)
                }
            }
        }
    }
    
    override suspend fun deleteTask(id: Int) {
        mutex.withLock {
            tasks.removeAll { it.id == id }
        }
    }
    
    override suspend fun deleteAllTasks() {
        mutex.withLock {
            tasks.clear()
            nextId = 1
        }
    }
}
