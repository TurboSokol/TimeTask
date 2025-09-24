/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.example.kmmreduxtemplate.components

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kmmreduxtemplate.screensStates.TaskItem
import com.example.kmmreduxtemplate.values.Colors
import com.example.kmmreduxtemplate.values.Dimensions

@Composable
fun CreateTaskBottomSheet(
    title: String,
    description: String,
    selectedColor: TaskItem.TaskColor,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onColorChange: (TaskItem.TaskColor) -> Unit,
    onCreateTask: (String, String, TaskItem.TaskColor) -> Unit,
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
        // Bottom sheet title
        Text(
            text = "Create New Task",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = Dimensions.paddingHalfMedium)
        )

        // Task title input
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Task Title") },
            placeholder = { Text("Enter task title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Task description input
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Task Description") },
            placeholder = { Text("Enter task description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

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
                .fillMaxWidth(),
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

            // Create button
            Button(
                onClick = { onCreateTask(title, description, selectedColor) },
                modifier = Modifier.weight(1f),
                enabled = title.isNotBlank()
            ) {
                Text("Create Task")
            }
        }

        // Bottom padding for better UX
        Spacer(modifier = Modifier.height(Dimensions.paddingMedium * 4))
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
