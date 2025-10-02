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
import com.turbosokol.TimeTask.localization.Strings
import com.turbosokol.TimeTask.repository.TaskRepository
import com.turbosokol.TimeTask.screensStates.TaskItem
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Simple task app for JS
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTaskApp() {
    val taskRepository: TaskRepository = koinInject()
    var tasks by remember { mutableStateOf<List<TaskItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Load tasks on startup
    LaunchedEffect(Unit) {
        try {
            val result = taskRepository.getTasks()
            if (result.isSuccess) {
                tasks = result.getOrNull() ?: emptyList()
            } else {
                println("Error loading tasks: ${result.exceptionOrNull()?.message}")
            }
        } catch (e: Exception) {
            println("Error loading tasks: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Strings.task_manager) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = Strings.add_task)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = Strings.no_tasks_yet,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = Strings.no_tasks_yet_message,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks) { task ->
                        TaskCard(
                            task = task,
                            onDelete = {
                                scope.launch {
                                    try {
                                        val result = taskRepository.deleteTask(task.id)
                                        if (result.isSuccess) {
                                            val tasksResult = taskRepository.getTasks()
                                            if (tasksResult.isSuccess) {
                                                tasks = tasksResult.getOrNull() ?: emptyList()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        println("Error deleting task: ${e.message}")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, description ->
                scope.launch {
                    try {
                        val result = taskRepository.createTask(
                            title = title,
                            description = description,
                            color = TaskItem.TaskColor.DEFAULT
                        )
                        if (result.isSuccess) {
                            val tasksResult = taskRepository.getTasks()
                            if (tasksResult.isSuccess) {
                                tasks = tasksResult.getOrNull() ?: emptyList()
                            }
                            showAddDialog = false
                        }
                    } catch (e: Exception) {
                        println("Error adding task: ${e.message}")
                    }
                }
            }
        )
    }
}

@Composable
fun TaskCard(
    task: TaskItem,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.headlineSmall
            )
            if (!task.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDelete
                ) {
                    Text(Strings.delete)
                }
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Strings.create_new_task) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(Strings.task_title) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(Strings.task_description) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAdd(title, description) },
                enabled = title.isNotBlank()
            ) {
                Text(Strings.add)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.cancel)
            }
        }
    )
}
