/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.notification

import com.turbosokol.TimeTask.screensStates.TaskItem

/**
 * No-op implementation of NotificationManager for platforms that don't support notifications
 * Used for iOS, WASM, and other non-Android platforms
 */
class NoOpNotificationManager : NotificationManager {
    
    override fun updateNotifications(tasks: List<TaskItem>) {
        // No-op for non-Android platforms
    }
    
    override fun stopNotifications() {
        // No-op for non-Android platforms
    }
    
    override fun isActive(): Boolean = false
}

