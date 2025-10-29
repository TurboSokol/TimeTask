/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.notification

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.turbosokol.TimeTask.notification.NotificationManager
import com.turbosokol.TimeTask.service.SimpleTaskNotificationService
import com.turbosokol.TimeTask.screensStates.TaskItem

/**
 * Android implementation of NotificationManager
 * Manages task notifications and integrates with the app's state
 * Handles starting/stopping foreground service based on active tasks
 */
class TaskNotificationManager(private val context: Context) : NotificationManager {
    
    private var isServiceRunning = false
    
    /**
     * Update notifications based on current task state
     * Note: tasks parameter now contains only active tasks from HomeScreen
     */
    override fun updateNotifications(tasks: List<TaskItem>) {
        val activeTasks = tasks // Already filtered in HomeScreen
        println("TaskNotificationManager: Updating notifications for ${activeTasks.size} active tasks")
        
        // Check notification permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                println("TaskNotificationManager: POST_NOTIFICATIONS permission not granted")
                return
            }
        }
        
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            println("TaskNotificationManager: Notifications are disabled in system settings")
            return
        }
        
        if (activeTasks.isNotEmpty()) {
            if (!isServiceRunning) {
                // Start foreground service
                println("TaskNotificationManager: Starting foreground service for ${activeTasks.size} active tasks")
                SimpleTaskNotificationService.startService(context, activeTasks)
                isServiceRunning = true
            } else {
                // Update existing service
                println("TaskNotificationManager: Updating existing service with ${activeTasks.size} active tasks")
                SimpleTaskNotificationService.updateTasks(context, activeTasks)
            }
        } else {
            if (isServiceRunning) {
                // Stop foreground service
                println("TaskNotificationManager: Stopping foreground service - no active tasks")
                SimpleTaskNotificationService.stopService(context)
                isServiceRunning = false
            }
        }
    }
    
    /**
     * Stop all notifications
     */
    override fun stopNotifications() {
        if (isServiceRunning) {
            SimpleTaskNotificationService.stopService(context)
            isServiceRunning = false
        }
    }
    
    /**
     * Check if service is currently running
     */
    override fun isActive(): Boolean = isServiceRunning
    
    private fun testBasicNotification() {
        try {
            val notificationManager = NotificationManagerCompat.from(context)
            
            // Create a simple test notification
            val notification = androidx.core.app.NotificationCompat.Builder(context, "task_timer_channel")
                .setContentTitle("TimeTask Test")
                .setContentText("Testing notification system")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setSilent(true)
                .setAutoCancel(true)
                .build()
            
            notificationManager.notify(9999, notification)
            println("TaskNotificationManager: Test notification sent")
        } catch (e: Exception) {
            println("TaskNotificationManager: Failed to send test notification: ${e.message}")
        }
    }
}
