/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.kmmreduxtemplate.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.turbosokol.kmmreduxtemplate.screensStates.TaskItem
import com.turbosokol.kmmreduxtemplate.values.Colors
import com.turbosokol.kmmreduxtemplate.values.Dimensions

@Composable
fun EditTaskBottomSheet(
    task: TaskItem,
    title: String,
    description: String,
    selectedColor: TaskItem.TaskColor,
    timeSeconds: String,
    timeHours: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onColorChange: (TaskItem.TaskColor) -> Unit,
    onTimeSecondsChange: (String) -> Unit,
    onTimeHoursChange: (String) -> Unit,
    onUpdateTask: (String, String, TaskItem.TaskColor, String, String) -> Unit,
    onDeleteTask: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                bottom = Dimensions.paddingSmall,
                start = Dimensions.paddingMedium,
                end = Dimensions.paddingMedium
            ),
        verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
    ) {
        // Header
        Text(
            text = "$title Edit Task",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = Dimensions.cornerMedium / 2)
        )

        // Title field
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Task Title") },
            placeholder = { Text("Enter task title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Description field
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Task Description") },
            placeholder = { Text("Enter task description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        // Time input fields
        Text(
            text = "Time Settings",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = Dimensions.paddingHalfMedium)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.cornerMedium)
        ) {
            // Time in seconds
            OutlinedTextField(
                value = timeSeconds,
                onValueChange = onTimeSecondsChange,
                label = { Text("Seconds") },
                placeholder = { Text("0") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Time in hours
            OutlinedTextField(
                value = timeHours,
                onValueChange = onTimeHoursChange,
                label = { Text("Hours") },
                placeholder = { Text("0.0") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        // Color picker
        Text(
            text = "Choose Color",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = Dimensions.paddingHalfMedium)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.cornerMedium)
        ) {
            TaskItem.TaskColor.entries.forEach { color ->
                ColorOption(
                    color = color,
                    isSelected = selectedColor == color,
                    onClick = { onColorChange(color) }
                )
            }
        }

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimensions.paddingSmall),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.cornerMedium)
        ) {
            // Cancel button
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Cancel")
            }

            // Delete button
            Button(
                onClick = onDeleteTask,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }

            // Update button
            Button(
                onClick = {
                    onUpdateTask(
                        title,
                        description,
                        selectedColor,
                        timeSeconds,
                        timeHours
                    )
                },
                modifier = Modifier.weight(1f),
                enabled = title.isNotBlank()
            ) {
                Text("Update")
            }
        }
    }
}

@Composable
private fun ColorOption(
    color: TaskItem.TaskColor,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorValue = getTaskColor(color)

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = colorValue,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
    )
}

private fun getTaskColor(color: TaskItem.TaskColor): Color {
    return when (color) {
        TaskItem.TaskColor.DEFAULT -> Colors.TaskColors.Default
        TaskItem.TaskColor.YELLOW -> Colors.TaskColors.Yellow
        TaskItem.TaskColor.PINK -> Colors.TaskColors.Pink
        TaskItem.TaskColor.BLUE -> Colors.TaskColors.Blue
        TaskItem.TaskColor.MINT -> Colors.TaskColors.Mint
    }
}
