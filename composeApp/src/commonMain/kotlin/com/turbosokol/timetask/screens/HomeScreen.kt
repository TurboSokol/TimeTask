package com.turbosokol.TimeTask.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.turbosokol.TimeTask.components.CreateTaskBottomSheet
import com.turbosokol.TimeTask.components.EditTaskBottomSheet
import com.turbosokol.TimeTask.localization.LocalizationManager
import com.turbosokol.TimeTask.notification.NotificationManager
import com.turbosokol.TimeTask.screensStates.HomeScreenAction
import com.turbosokol.TimeTask.screensStates.TaskItem
import com.turbosokol.TimeTask.values.Colors
import com.turbosokol.TimeTask.values.Dimensions
import com.turbosokol.TimeTask.viewmodel.ReduxViewModel
import org.koin.compose.koinInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import timetask.composeapp.generated.resources.Res
import timetask.composeapp.generated.resources.leaves_background

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: ReduxViewModel) {

    // Observe state from the store
    val appState by viewModel.store.observeState().collectAsState()
    val homeState = appState.getHomeScreenState()
    
    // Get notification manager
    val notificationManager: NotificationManager = koinInject()

    // Load tasks from database when screen first appears
    LaunchedEffect(Unit) {
        viewModel.execute(HomeScreenAction.LoadTasks)
    }

    // Safety timeout to ensure firstLaunch is always set to false
    LaunchedEffect(homeState.firstLaunch) {
        if (homeState.firstLaunch) {
            delay(1000) // 1 seconds timeout
            val currentState = viewModel.store.observeState().value.getHomeScreenState()
            if (currentState.firstLaunch) {
                // Force empty state if still in firstLaunch after timeout
                viewModel.execute(HomeScreenAction.TasksLoaded(emptyList()))
            }
        }
    }
    
    // Update notifications when tasks change or when any task state changes
    LaunchedEffect(
        homeState.tasks,
        homeState.tasks.map { "${it.id}-${it.isActive}-${it.timeSeconds}" }
    ) {
        println("HomeScreen: LaunchedEffect triggered - updating notifications")
        notificationManager.updateNotifications(homeState.tasks)
    }

    // Bottom sheet state management
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    var isCreatingTask by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(TaskItem.TaskColor.DEFAULT) }
    var taskTimeSeconds by remember { mutableStateOf("") }
    var taskTimeHours by remember { mutableStateOf("") }
    var editingTask by remember { mutableStateOf<TaskItem?>(null) }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            Column(
                modifier = Modifier.imePadding()
            ) {
                if (isCreatingTask) {
                    CreateTaskBottomSheet(
                    title = taskTitle,
                    description = taskDescription,
                    selectedColor = selectedColor,
                    onTitleChange = { taskTitle = it },
                    onDescriptionChange = { taskDescription = it },
                    onColorChange = { selectedColor = it },
                    onCreateTask = { title, description, color ->
                        if (title.isNotBlank()) {
                            viewModel.execute(
                                HomeScreenAction.CreateTask(
                                    title,
                                    description,
                                    color
                                )
                            )
                            isCreatingTask = false
                            taskTitle = ""
                            taskDescription = ""
                            selectedColor = TaskItem.TaskColor.DEFAULT
                            coroutineScope.launch {
                                bottomSheetScaffoldState.bottomSheetState.partialExpand()
                            }
                        }
                    },
                    onCancel = {
                        isCreatingTask = false
                        taskTitle = ""
                        taskDescription = ""
                        selectedColor = TaskItem.TaskColor.DEFAULT
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.partialExpand()
                        }
                    }
                )
            } else if (editingTask != null) {
                EditTaskBottomSheet(
                    task = editingTask!!,
                    title = taskTitle,
                    description = taskDescription,
                    selectedColor = selectedColor,
                    timeSeconds = taskTimeSeconds,
                    timeHours = taskTimeHours,
                    onTitleChange = { taskTitle = it },
                    onDescriptionChange = { taskDescription = it },
                    onColorChange = { selectedColor = it },
                    onTimeSecondsChange = { taskTimeSeconds = it },
                    onTimeHoursChange = { taskTimeHours = it },
                    onUpdateTask = { title, description, color, timeSeconds, timeHours ->
                        if (title.isNotBlank()) {
                            val parsedSeconds = timeSeconds.toLongOrNull() ?: 0L
                            val parsedHours = timeHours.toDoubleOrNull() ?: 0.0
                            viewModel.execute(
                                HomeScreenAction.EditTask(
                                    editingTask!!.id,
                                    title,
                                    description,
                                    color,
                                    parsedSeconds,
                                    parsedHours
                                )
                            )
                            editingTask = null
                            taskTitle = ""
                            taskDescription = ""
                            selectedColor = TaskItem.TaskColor.DEFAULT
                            taskTimeSeconds = ""
                            taskTimeHours = ""
                            coroutineScope.launch {
                                bottomSheetScaffoldState.bottomSheetState.partialExpand()
                            }
                        }
                    },
                    onDeleteTask = {
                        viewModel.execute(HomeScreenAction.DeleteTask(editingTask!!.id))
                        editingTask = null
                        taskTitle = ""
                        taskDescription = ""
                        selectedColor = TaskItem.TaskColor.DEFAULT
                        taskTimeSeconds = ""
                        taskTimeHours = ""
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.partialExpand()
                        }
                    },
                    onCancel = {
                        editingTask = null
                        taskTitle = ""
                        taskDescription = ""
                        selectedColor = TaskItem.TaskColor.DEFAULT
                        taskTimeSeconds = ""
                        taskTimeHours = ""
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.partialExpand()
                        }
                    }
                )
                } else {
                    // Empty state when no bottom sheet should be shown
                    Spacer(modifier = Modifier.height(1.dp))
                }
            }
        },
        sheetPeekHeight = 0.dp
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background image
            Image(
                painter = painterResource(Res.drawable.leaves_background),
                contentDescription = LocalizationManager.getString("background"),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.4f
            )

            // Content over background
            Column(
                modifier = Modifier
                    .safeContentPadding()
                    .fillMaxSize()
            ) {
                // Header
                Text(
                    text = LocalizationManager.getString("task_manager"),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        bottom = Dimensions.paddingSmall,
                        start = Dimensions.paddingSmall,
                        end = Dimensions.paddingSmall
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Main content
                if (homeState.isLoading && homeState.firstLaunch) {
                    // Loading state - only show during initial launch
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = LocalizationManager.getString("loading_tasks"),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                } else if (homeState.error != null) {
                    // Error state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${LocalizationManager.getString("error_prefix").replace("%s", "")}${homeState.error ?: ""}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(
                                onClick = { viewModel.execute(HomeScreenAction.LoadTasks) },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text(LocalizationManager.getString("retry"))
                            }
                        }
                    }
                } else if (homeState.tasks.isEmpty() && !homeState.firstLaunch) {
                    // Empty state - only show after initial database load is complete
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Big + button
                            FloatingActionButton(
                                onClick = {
                                    // Prepare for creating new task
                                    isCreatingTask = true
                                    editingTask = null
                                    taskTitle = ""
                                    taskDescription = ""
                                    selectedColor = TaskItem.TaskColor.DEFAULT
                                    taskTimeSeconds = ""
                                    taskTimeHours = ""
                                    coroutineScope.launch {
                                        bottomSheetScaffoldState.bottomSheetState.expand()
                                    }
                                },
                                modifier = Modifier
                                    .padding(bottom = 24.dp)
                                    .height(80.dp)
                                    .width(80.dp),
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = LocalizationManager.getString("add_task_content_desc"),
                                    modifier = Modifier.height(40.dp).width(40.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            
                            Text(
                                text = LocalizationManager.getString("no_tasks_yet"),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = LocalizationManager.getString("no_tasks_yet_message"),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                } else {
                    // Tasks list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.cornerMedium)
                    ) {
                        items(homeState.tasks) { task ->
                            TaskItemCard(
                                task = task,
                                viewModel = viewModel,
                                onTaskClick = { taskToEdit ->
                                    editingTask = taskToEdit
                                    // Set up edit task state
                                    isCreatingTask = false
                                    editingTask = task
                                    taskTitle = task.title
                                    taskDescription = task.description
                                    selectedColor = task.color
                                    taskTimeSeconds = task.timeSeconds.toString()
                                    taskTimeHours = task.timeHours.toString()
                                    coroutineScope.launch {
                                        bottomSheetScaffoldState.bottomSheetState.expand()
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Floating Action Button overlay
            FloatingActionButton(
                onClick = {
                    isCreatingTask = true
                    editingTask = null
                    taskTitle = ""
                    taskDescription = ""
                    selectedColor = TaskItem.TaskColor.DEFAULT
                    coroutineScope.launch {
                        bottomSheetScaffoldState.bottomSheetState.expand()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = LocalizationManager.getString("add_task")
                )
            }
        }
    }

}

@Composable
fun TaskItemCard(
    task: TaskItem,
    viewModel: ReduxViewModel,
    onTaskClick: (TaskItem) -> Unit
) {
    // Timer logic using LaunchedEffect with task.id as key to avoid recreation on state changes
    LaunchedEffect(key1 = task.id, key2 = task.isActive) {
        if (task.isActive) {
            // Start from current time and increment
            var currentSeconds = task.timeSeconds
            while (task.isActive) {
                delay(1000L) // Wait for 1 second
                currentSeconds += 1
                val newHours = currentSeconds / 3600.0
                viewModel.execute(
                    HomeScreenAction.UpdateTaskTime(
                        task.id,
                        currentSeconds,
                        newHours
                    )
                )
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.paddingMicro)
            .clickable { onTaskClick(task) },
        shape = RoundedCornerShape(Dimensions.paddingSmall),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.paddingMicro),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 2.dp,
            color = getTaskColor(task.color).copy(
                alpha = if (task.isActive) 1.0f else 0.3f
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingSmall)
        ) {
            // Task title and reset button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                // Reset button
                IconButton(
                    onClick = { viewModel.execute(HomeScreenAction.ResetTaskTime(task.id)) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = LocalizationManager.getString("reset_time"),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Dimensions.paddingMicro)
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            // Timer display and controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Timer display
                Column {
                    Text(
                        text = formatTime(task.timeSeconds),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (task.isActive)
                            getTaskColor(task.color)
                        else
                            getTaskColor(task.color).copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${LocalizationManager.getString("hours_display").replace("%.1f", "")} ${(task.timeHours * 10).toInt() / 10.0}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (task.isActive)
                            getTaskColor(task.color)
                        else
                            getTaskColor(task.color).copy(alpha = 0.7f)
                    )
                }

                // Start/Pause button
                Button(
                    onClick = { viewModel.execute(HomeScreenAction.ToggleTaskTimer(task.id)) },
                    modifier = Modifier.width(120.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = getTaskColor(task.color),
                        contentColor = Colors.UI.White
                    )
                ) {
                    Icon(
                        imageVector = if (task.isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (task.isActive) LocalizationManager.getString("pause") else LocalizationManager.getString("start"),
                        modifier = Modifier.padding(end = Dimensions.paddingMicro)
                    )
                    Text(
                        text = if (task.isActive) LocalizationManager.getString("pause") else LocalizationManager.getString("start")
                    )

                }
            }
        }
    }
}

// Helper function to format time in HH:MM:SS format
private fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return "${hours.toString().padStart(2, '0')}:${
        minutes.toString().padStart(2, '0')
    }:${secs.toString().padStart(2, '0')}"
}

private fun getTaskColor(color: TaskItem.TaskColor): androidx.compose.ui.graphics.Color {
    return when (color) {
        TaskItem.TaskColor.DEFAULT -> Colors.TaskColors.Default
        TaskItem.TaskColor.BROWN -> Colors.TaskColors.Brown
        TaskItem.TaskColor.PINK -> Colors.TaskColors.Pink
        TaskItem.TaskColor.BLUE -> Colors.TaskColors.Blue
        TaskItem.TaskColor.BLACK -> Colors.TaskColors.Black
        TaskItem.TaskColor.ORANGE -> Colors.TaskColors.Orange
        TaskItem.TaskColor.LIME -> Colors.TaskColors.Lime
        TaskItem.TaskColor.TEAL -> Colors.TaskColors.Teal
    }
}
