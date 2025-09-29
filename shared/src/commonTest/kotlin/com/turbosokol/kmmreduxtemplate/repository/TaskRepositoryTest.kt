/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.kmmreduxtemplate.repository

import com.turbosokol.kmmreduxtemplate.repository.datasource.InMemoryLocalTaskDataSource
import com.turbosokol.kmmreduxtemplate.repository.datasource.NetworkRemoteTaskDataSource
import com.turbosokol.kmmreduxtemplate.screensStates.TaskItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Test suite for TaskRepository functionality
 * Tests cache-first strategy, CRUD operations, and error handling
 */
class TaskRepositoryTest {
    
    private fun createRepository(): TaskRepository {
        return TaskRepositoryImpl(
            localDataSource = InMemoryLocalTaskDataSource(),
            remoteDataSource = NetworkRemoteTaskDataSource(),
            ioDispatcher = kotlinx.coroutines.test.TestCoroutineDispatcher()
        )
    }
    
    @Test
    fun `create task should work with valid data`() = runTest {
        val repository = createRepository()
        
        val result = repository.createTask(
            title = "Test Task",
            description = "Test Description",
            color = TaskItem.TaskColor.BLUE
        )
        
        assertTrue(result.isSuccess)
        val task = result.getOrNull()
        assertNotNull(task)
        assertEquals("Test Task", task.title)
        assertEquals("Test Description", task.description)
        assertEquals(TaskItem.TaskColor.BLUE, task.color)
        assertEquals(0L, task.timeSeconds)
        assertEquals(0.0, task.timeHours)
        assertEquals(false, task.isActive)
    }
    
    @Test
    fun `get tasks should return created tasks`() = run {
        val repository = createRepository()
        
        // Create a task
        repository.createTask(
            title = "Test Task 1",
            description = "Description 1",
            color = TaskItem.TaskColor.DEFAULT
        )
        
        repository.createTask(
            title = "Test Task 2", 
            description = "Description 2",
            color = TaskItem.TaskColor.MINT
        )
        
        // Get all tasks
        val result = repository.getTasks()
        assertTrue(result.isSuccess)
        
        val tasks = result.getOrNull()
        assertNotNull(tasks)
        assertEquals(2, tasks.size)
        
        val firstTask = tasks.find { it.title == "Test Task 1" }
        assertNotNull(firstTask)
        assertEquals("Description 1", firstTask.description)
        
        val secondTask = tasks.find { it.title == "Test Task 2" }
        assertNotNull(secondTask)
        assertEquals("Description 2", secondTask.description)
    }
    
    @Test
    fun `update task should modify existing task`() = run {
        val repository = createRepository()
        
        // Create a task
        val createResult = repository.createTask(
            title = "Original Title",
            description = "Original Description",
            color = TaskItem.TaskColor.DEFAULT
        )
        
        val originalTask = createResult.getOrThrow()
        
        // Update the task
        val updatedTask = originalTask.copy(
            title = "Updated Title",
            description = "Updated Description",
            color = TaskItem.TaskColor.PINK,
            timeSeconds = 3600L,
            timeHours = 1.0
        )
        
        val updateResult = repository.updateTask(updatedTask)
        assertTrue(updateResult.isSuccess)
        
        // Verify the update
        val taskResult = repository.getTaskById(originalTask.id)
        assertTrue(taskResult.isSuccess)
        
        val retrievedTask = taskResult.getOrNull()
        assertNotNull(retrievedTask)
        assertEquals("Updated Title", retrievedTask.title)
        assertEquals("Updated Description", retrievedTask.description)
        assertEquals(TaskItem.TaskColor.PINK, retrievedTask.color)
        assertEquals(3600L, retrievedTask.timeSeconds)
        assertEquals(1.0, retrievedTask.timeHours)
    }
    
    @Test
    fun `delete task should remove task`() = run {
        val repository = createRepository()
        
        // Create a task
        val createResult = repository.createTask(
            title = "Task to Delete",
            description = "This will be deleted",
            color = TaskItem.TaskColor.BLUE
        )
        
        val task = createResult.getOrThrow()
        
        // Verify task exists
        val beforeDelete = repository.getTasks()
        assertEquals(1, beforeDelete.getOrThrow().size)
        
        // Delete the task
        val deleteResult = repository.deleteTask(task.id)
        assertTrue(deleteResult.isSuccess)
        
        // Verify task is gone
        val afterDelete = repository.getTasks()
        assertEquals(0, afterDelete.getOrThrow().size)
        
        val taskByIdResult = repository.getTaskById(task.id)
        assertTrue(taskByIdResult.isSuccess)
        assertEquals(null, taskByIdResult.getOrNull())
    }
    
    @Test
    fun `clear all data should remove all tasks`() = run {
        val repository = createRepository()
        
        // Create multiple tasks
        repository.createTask("Task 1", "Desc 1", TaskItem.TaskColor.DEFAULT)
        repository.createTask("Task 2", "Desc 2", TaskItem.TaskColor.BLUE)
        repository.createTask("Task 3", "Desc 3", TaskItem.TaskColor.MINT)
        
        // Verify tasks exist
        val beforeClear = repository.getTasks()
        assertEquals(3, beforeClear.getOrThrow().size)
        
        // Clear all data
        val clearResult = repository.clearAllData()
        assertTrue(clearResult.isSuccess)
        
        // Verify all tasks are gone
        val afterClear = repository.getTasks()
        assertEquals(0, afterClear.getOrThrow().size)
    }
}

