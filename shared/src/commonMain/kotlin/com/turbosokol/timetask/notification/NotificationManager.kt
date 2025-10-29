/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.notification

import com.turbosokol.TimeTask.screensStates.TaskItem

/**
 * Common interface for notification management across platforms
 */
interface NotificationManager {
    
    /**
     * Update notifications based on current task state
     */
    fun updateNotifications(tasks: List<TaskItem>)
    
    /**
     * Stop all notifications
     */
    fun stopNotifications()
    
    /**
     * Check if notifications are currently active
     */
    fun isActive(): Boolean
}

