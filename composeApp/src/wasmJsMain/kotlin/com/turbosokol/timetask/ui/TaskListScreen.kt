/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.turbosokol.TimeTask.data.Task
import com.turbosokol.TimeTask.localization.LocalizationManager
import com.turbosokol.TimeTask.repository.TaskRepository
import org.koin.compose.koinInject

/**
 * Simple task list screen for WASM
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen() {
    val taskRepository: TaskRepository = koinInject()
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    
    // Collect tasks from repository
    LaunchedEffect(Unit) {
        taskRepository.getAllTasks().collect { taskList ->
            tasks = taskList
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(LocalizationManager.getString("timetask_wasm")) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = LocalizationManager.getString("add_task"))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = LocalizationManager.getString("no_tasks_yet_wasm"),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            onToggleComplete = { taskRepository.toggleTaskCompletion(task.id) },
                            onDelete = { taskRepository.deleteTask(task.id) }
                        )
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onAddTask = { title, description ->
                taskRepository.addTask(title, description)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggleComplete: suspend () -> Unit,
    onDelete: suspend () -> Unit
) {
    var isCompleted by remember { mutableStateOf(task.isCompleted) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { 
                    isCompleted = !isCompleted
                    // Note: In a real app, you'd handle the async operation properly
                }
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                task.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            TextButton(
                onClick = { 
                    // Note: In a real app, you'd handle the async operation properly
                }
            ) {
                Text(LocalizationManager.getString("delete"))
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (String, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(LocalizationManager.getString("add_new_task")) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(LocalizationManager.getString("title")) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(LocalizationManager.getString("description_optional")) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    if (title.isNotBlank()) {
                        onAddTask(title, description.takeIf { it.isNotBlank() })
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text(LocalizationManager.getString("add"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(LocalizationManager.getString("cancel"))
            }
        }
    )
}







