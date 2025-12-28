package com.turbosokol.TimeTask.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.turbosokol.TimeTask.notification.NotificationManager
import com.turbosokol.TimeTask.screensStates.TaskItem

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/
/**
 * Android implementation of NotificationManager
 * Manages task notifications and integrates with the app's state
 * Handles starting/stopping foreground service based on active tasks
 */
class TaskNotificationInteractor(
    private val context: Context
) : NotificationManager {

    private var isServiceRunning = false

    /**
     * Update notifications based on current task state
     * Note: tasks parameter now contains only active tasks from HomeScreen
     */
    override fun updateNotifications(tasks: List<TaskItem>) {
        println("TaskNotificationManager: Updating notifications for ${tasks.size} active tasks")

        // Check notification permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                println("TaskNotificationManager: POST_NOTIFICATIONS permission not granted")
                return
            }
        }

        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            println("TaskNotificationManager: Notifications are disabled in system settings")
            return
        }

        if (tasks.isNotEmpty()) {
            if (!isServiceRunning) {
                // Start foreground service
                println("TaskNotificationManager: Starting foreground service for ${tasks.size} active tasks")
                TaskNotificationService.startService(context, tasks)
                isServiceRunning = true
            } else {
                // Update existing service
                println("TaskNotificationManager: Updating existing service with ${tasks.size} active tasks")
                TaskNotificationService.updateTasks(context, tasks)
            }
        } else {
            if (isServiceRunning) {
                // Stop foreground service
                println("TaskNotificationManager: Stopping foreground service - no active tasks")
                TaskNotificationService.stopService(context)
                isServiceRunning = false
            }
        }
    }

    /**
     * Stop all notifications
     */
    override fun stopNotifications() {
        if (isServiceRunning) {
            TaskNotificationService.stopService(context)
            isServiceRunning = false
        }
    }

    /**
     * Check if service is currently running
     */
    override fun isActive(): Boolean = isServiceRunning
}