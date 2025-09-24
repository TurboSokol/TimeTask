package com.example.kmmreduxtemplate.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.kmmreduxtemplate.viewmodel.ReduxViewModel
import com.example.kmmreduxtemplate.values.Dimensions
import com.example.kmmreduxtemplate.values.Colors
import com.example.kmmreduxtemplate.screensStates.TaskItem
import com.example.kmmreduxtemplate.screensStates.HomeScreenAction
import com.example.kmmreduxtemplate.components.CreateTaskBottomSheet
import com.example.kmmreduxtemplate.components.EditTaskBottomSheet
import kmmreduxtemplate.composeapp.generated.resources.Res
import kmmreduxtemplate.composeapp.generated.resources.leaves_background
import org.jetbrains.compose.resources.painterResource

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

    // Bottom sheet state management
    var showCreateBottomSheet by remember { mutableStateOf(false) }
    var showEditBottomSheet by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(TaskItem.TaskColor.DEFAULT) }
    var taskTimeSeconds by remember { mutableStateOf("") }
    var taskTimeHours by remember { mutableStateOf("") }
    var editingTask by remember { mutableStateOf<TaskItem?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateBottomSheet = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background image
            Image(
                painter = painterResource(Res.drawable.leaves_background),
                contentDescription = "Background",
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
                    text = "Task Manager",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        bottom = Dimensions.paddingSmall,
                        start = Dimensions.paddingSmall,
                        end = Dimensions.paddingSmall
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Vertical RecyclerView (LazyColumn)
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
                                taskTitle = taskToEdit.title
                                taskDescription = taskToEdit.description
                                selectedColor = taskToEdit.color
                                taskTimeSeconds = taskToEdit.timeSeconds.toString()
                                taskTimeHours = taskToEdit.timeHours.toString()
                                showEditBottomSheet = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Bottom Sheet for creating new task
    if (showCreateBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showCreateBottomSheet = false
                taskTitle = ""
                taskDescription = ""
                selectedColor = TaskItem.TaskColor.DEFAULT
            },
            modifier = Modifier.wrapContentHeight(),
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            CreateTaskBottomSheet(
                title = taskTitle,
                description = taskDescription,
                selectedColor = selectedColor,
                onTitleChange = { taskTitle = it },
                onDescriptionChange = { taskDescription = it },
                onColorChange = { selectedColor = it },
                onCreateTask = { title, description, color ->
                    if (title.isNotBlank()) {
                        viewModel.execute(HomeScreenAction.CreateTask(title, description, color))
                        showCreateBottomSheet = false
                        taskTitle = ""
                        taskDescription = ""
                        selectedColor = TaskItem.TaskColor.DEFAULT
                    }
                },
                onCancel = {
                    showCreateBottomSheet = false
                    taskTitle = ""
                    taskDescription = ""
                    selectedColor = TaskItem.TaskColor.DEFAULT
                }
            )
        }
    }

    // Bottom Sheet for editing/deleting task
    if (showEditBottomSheet && editingTask != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showEditBottomSheet = false
                editingTask = null
                taskTitle = ""
                taskDescription = ""
                selectedColor = TaskItem.TaskColor.DEFAULT
                taskTimeSeconds = ""
                taskTimeHours = ""
            },
            modifier = Modifier.wrapContentHeight(),
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
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
                        showEditBottomSheet = false
                        editingTask = null
                        taskTitle = ""
                        taskDescription = ""
                        selectedColor = TaskItem.TaskColor.DEFAULT
                        taskTimeSeconds = ""
                        taskTimeHours = ""
                    }
                },
                onDeleteTask = {
                    viewModel.execute(HomeScreenAction.DeleteTask(editingTask!!.id))
                    showEditBottomSheet = false
                    editingTask = null
                    taskTitle = ""
                    taskDescription = ""
                    selectedColor = TaskItem.TaskColor.DEFAULT
                    taskTimeSeconds = ""
                    taskTimeHours = ""
                },
                onCancel = {
                    showEditBottomSheet = false
                    editingTask = null
                    taskTitle = ""
                    taskDescription = ""
                    selectedColor = TaskItem.TaskColor.DEFAULT
                    taskTimeSeconds = ""
                    taskTimeHours = ""
                }
            )
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
                alpha = if (task.isActive) 0.8f else 0.3f
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
                        contentDescription = "Reset time",
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
                        text = "${(task.timeHours * 10).toInt() / 10.0} hours",
                        style = MaterialTheme.typography.bodySmall,
                        color = getTaskColor(task.color).copy(alpha = 0.6f)
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
                        contentDescription = if (task.isActive) "Pause" else "Start",
                        modifier = Modifier.padding(end = Dimensions.paddingMicro)
                    )
                    Text(
                        text = if (task.isActive) "Pause" else "Start"
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
        TaskItem.TaskColor.YELLOW -> Colors.TaskColors.Yellow
        TaskItem.TaskColor.PINK -> Colors.TaskColors.Pink
        TaskItem.TaskColor.BLUE -> Colors.TaskColors.Blue
        TaskItem.TaskColor.MINT -> Colors.TaskColors.Mint
    }
}
