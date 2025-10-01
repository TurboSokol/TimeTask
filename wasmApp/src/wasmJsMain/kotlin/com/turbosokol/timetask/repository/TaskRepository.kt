/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.repository

import com.turbosokol.TimeTask.data.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

/**
 * Simple in-memory task repository for WASM
 */
class TaskRepository {
    private val tasks = mutableMapOf<Int, Task>()
    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    private val mutex = Mutex()
    private var nextId = 1
    
    /**
     * Get all tasks as a Flow
     */
    fun getAllTasks(): Flow<List<Task>> = tasksFlow.asStateFlow()
    
    /**
     * Get a task by ID
     */
    suspend fun getTaskById(id: Int): Task? = mutex.withLock {
        tasks[id]
    }
    
    /**
     * Add a new task
     */
    suspend fun addTask(title: String, description: String? = null): Task = mutex.withLock {
        val now = Clock.System.now()
        val task = Task(
            id = nextId++,
            title = title,
            description = description,
            isCompleted = false,
            createdAt = now,
            updatedAt = now
        )
        tasks[task.id] = task
        updateTasksFlow()
        task
    }
    
    /**
     * Update an existing task
     */
    suspend fun updateTask(task: Task): Task = mutex.withLock {
        val updatedTask = task.copy(updatedAt = Clock.System.now())
        tasks[updatedTask.id] = updatedTask
        updateTasksFlow()
        updatedTask
    }
    
    /**
     * Delete a task
     */
    suspend fun deleteTask(id: Int): Boolean = mutex.withLock {
        val removed = tasks.remove(id) != null
        if (removed) {
            updateTasksFlow()
        }
        removed
    }
    
    /**
     * Toggle task completion status
     */
    suspend fun toggleTaskCompletion(id: Int): Task? = mutex.withLock {
        val task = tasks[id] ?: return null
        val updatedTask = task.copy(
            isCompleted = !task.isCompleted,
            updatedAt = Clock.System.now()
        )
        tasks[updatedTask.id] = updatedTask
        updateTasksFlow()
        updatedTask
    }
    
    private fun updateTasksFlow() {
        tasksFlow.value = tasks.values.toList().sortedBy { it.createdAt }
    }
}






