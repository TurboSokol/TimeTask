/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.notification

import com.turbosokol.TimeTask.screensStates.TaskItem
import platform.Foundation.NSLog
import platform.UserNotifications.*
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * iOS-specific implementation of NotificationManager
 * Uses UserNotifications framework for local notifications
 */
@OptIn(ExperimentalForeignApi::class)
class IosNotificationManager : NotificationManager {
    
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    private var isActive = false
    
    init {
        NSLog("IosNotificationManager: Initializing iOS notification manager")
        setupNotificationCenter()
    }
    
    override fun updateNotifications(tasks: List<TaskItem>) {
        NSLog("IosNotificationManager: Updating notifications for ${tasks.size} active tasks")
        
        val activeTasks = tasks // Already filtered in HomeScreen
        NSLog("IosNotificationManager: Processing ${activeTasks.size} active tasks")
        
        if (activeTasks.isEmpty()) {
            stopNotifications()
            return
        }
        
        // Cancel all existing notifications and create new ones
        // This ensures live updates by replacing notifications
        notificationCenter.removeAllPendingNotificationRequests()
        notificationCenter.removeAllDeliveredNotifications()
        
        // Create notifications for each active task
        activeTasks.forEach { task ->
            createTaskNotification(task)
        }
        
        isActive = true
        NSLog("IosNotificationManager: Updated ${activeTasks.size} task notifications")
    }
    
    override fun stopNotifications() {
        NSLog("IosNotificationManager: Stopping all notifications")
        notificationCenter.removeAllPendingNotificationRequests()
        notificationCenter.removeAllDeliveredNotifications()
        isActive = false
    }
    
    override fun isActive(): Boolean = isActive
    
    private fun setupNotificationCenter() {
        NSLog("IosNotificationManager: Notification center setup complete")
    }
    
    private fun createTaskNotification(task: TaskItem) {
        val content = UNMutableNotificationContent()
        
        // Match Android notification format exactly
        content.setTitle(task.title)
        content.setBody(formatTime(task.timeSeconds)) // Main time display
        content.setSubtitle("${formatHours(task.timeHours)}") // Hours as subtitle
        
        // Configure for lock screen display (like Android)
        content.setSound(null) // Silent notifications
        content.setBadge(null) // No badge
        content.setThreadIdentifier("task_timers") // Group related notifications
        
        // Set category identifier for task notifications
        content.setCategoryIdentifier("TASK_NOTIFICATION")
        
        // Add custom data for app interaction
        content.setUserInfo(mapOf(
            "taskId" to task.id.toString(),
            "taskTitle" to task.title,
            "timeSeconds" to task.timeSeconds.toString(),
            "timeHours" to task.timeHours.toString()
        ))
        
        // Create immediate trigger (notification shows right away)
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(0.1, false)
        
        // Create request with unique identifier
        val request = UNNotificationRequest.requestWithIdentifier(
            "task_${task.id}",
            content,
            trigger
        )
        
        // Schedule the notification
        notificationCenter.addNotificationRequest(request) { error ->
            if (error != null) {
                NSLog("IosNotificationManager: Failed to schedule notification for task ${task.id}: ${error.localizedDescription}")
            } else {
                NSLog("IosNotificationManager: Successfully scheduled notification for task ${task.id}")
            }
        }
    }
    
    private fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    }
    
    private fun formatHours(hours: Double): String {
        val formatted = (hours * 100).toInt() / 100.0
        return "${formatted} h"
    }
}
