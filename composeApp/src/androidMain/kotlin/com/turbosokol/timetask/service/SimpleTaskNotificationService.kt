/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.turbosokol.TimeTask.MainActivity
import com.turbosokol.TimeTask.data.TaskItemParcelable
import com.turbosokol.TimeTask.screensStates.TaskItem
import com.turbosokol.TimeTask.values.Colors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Simplified foreground service that shows persistent notifications for active tasks
 */
class SimpleTaskNotificationService : Service() {
    
    companion object {
        private const val NOTIFICATION_ID_BASE = 1000
        private const val CHANNEL_ID = "task_timer_channel"
        private const val CHANNEL_NAME = "Task Timer Notifications"
        private const val CHANNEL_DESCRIPTION = "Shows silent high-priority notifications for active task timers with task name, time, and hours"
        
        // Actions for notification
        private const val ACTION_OPEN_APP = "open_app"
        
        private var activeTasks = mutableMapOf<Int, TaskItem>()
        private var updateJob: Job? = null
        
        fun startService(context: Context, tasks: List<TaskItem>) {
            val intent = Intent(context, SimpleTaskNotificationService::class.java)
            intent.putParcelableArrayListExtra("tasks", ArrayList(tasks.map { task ->
                TaskItemParcelable(
                    id = task.id,
                    title = task.title,
                    description = task.description,
                    isActive = task.isActive,
                    timeSeconds = task.timeSeconds,
                    timeHours = task.timeHours,
                    color = task.color.name
                )
            }))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stopService(context: Context) {
            val intent = Intent(context, SimpleTaskNotificationService::class.java)
            context.stopService(intent)
        }
        
        fun updateTasks(context: Context, tasks: List<TaskItem>) {
            val intent = Intent(context, SimpleTaskNotificationService::class.java)
            intent.action = "UPDATE_TASKS"
            intent.putParcelableArrayListExtra("tasks", ArrayList(tasks.map { task ->
                TaskItemParcelable(
                    id = task.id,
                    title = task.title,
                    description = task.description,
                    isActive = task.isActive,
                    timeSeconds = task.timeSeconds,
                    timeHours = task.timeHours,
                    color = task.color.name
                )
            }))
            context.startService(intent)
        }
    }
    
    private lateinit var notificationManager: NotificationManager
    private val serviceScope = CoroutineScope(Dispatchers.Main)
    
    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("SimpleTaskNotificationService: onStartCommand called with action: ${intent?.action}")
        when (intent?.action) {
            "UPDATE_TASKS" -> {
                val tasks = intent.getParcelableArrayListExtra<TaskItemParcelable>("tasks")?.map { parcelable ->
                    TaskItem(
                        id = parcelable.id,
                        title = parcelable.title,
                        description = parcelable.description,
                        isActive = parcelable.isActive,
                        timeSeconds = parcelable.timeSeconds,
                        timeHours = parcelable.timeHours,
                        color = try {
                            TaskItem.TaskColor.valueOf(parcelable.color)
                        } catch (e: IllegalArgumentException) {
                            TaskItem.TaskColor.DEFAULT
                        }
                    )
                } ?: emptyList()
                updateActiveTasks(tasks)
            }
            ACTION_OPEN_APP -> {
                val openAppIntent = Intent(this, MainActivity::class.java)
                openAppIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(openAppIntent)
            }
            else -> {
                val tasks = intent?.getParcelableArrayListExtra<TaskItemParcelable>("tasks")?.map { parcelable ->
                    TaskItem(
                        id = parcelable.id,
                        title = parcelable.title,
                        description = parcelable.description,
                        isActive = parcelable.isActive,
                        timeSeconds = parcelable.timeSeconds,
                        timeHours = parcelable.timeHours,
                        color = try {
                            TaskItem.TaskColor.valueOf(parcelable.color)
                        } catch (e: IllegalArgumentException) {
                            TaskItem.TaskColor.DEFAULT
                        }
                    )
                } ?: emptyList()
                startForegroundService(tasks)
            }
        }
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                setShowBadge(true)
                enableLights(true)
                lightColor = android.graphics.Color.BLUE
                enableVibration(false)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun startForegroundService(tasks: List<TaskItem>) {
        println("SimpleTaskNotificationService: startForegroundService called with ${tasks.size} tasks")
        activeTasks.clear()
        // Note: tasks parameter now contains only active tasks from TaskNotificationManager
        tasks.forEach { task ->
            activeTasks[task.id] = task
        }
        
        println("SimpleTaskNotificationService: Found ${activeTasks.size} active tasks")
        if (activeTasks.isNotEmpty()) {
            // Start foreground service with the first task's notification
            val firstTask = activeTasks.values.first()
            val mainNotification = createTaskNotification(firstTask)
            startForeground(NOTIFICATION_ID_BASE + firstTask.id, mainNotification)
            
            // Create notifications for all active tasks
            createTaskNotifications()
            startTimerUpdates()
            println("SimpleTaskNotificationService: Started foreground service with notifications")
        } else {
            println("SimpleTaskNotificationService: No active tasks, stopping service")
            stopSelf()
        }
    }
    
    private fun updateActiveTasks(tasks: List<TaskItem>) {
        // Note: tasks parameter now contains only active tasks from TaskNotificationManager
        val newActiveTasks = tasks
        val currentActiveIds = activeTasks.keys.toSet()
        val newActiveIds = newActiveTasks.map { it.id }.toSet()
        
        println("SimpleTaskNotificationService: updateActiveTasks - Current active: $currentActiveIds, New active: $newActiveIds")
        
        // Update existing tasks
        newActiveTasks.forEach { task ->
            activeTasks[task.id] = task
        }
        
        // Remove stopped tasks and cancel their notifications
        val stoppedTasks = currentActiveIds - newActiveIds
        stoppedTasks.forEach { taskId ->
            println("SimpleTaskNotificationService: Task $taskId stopped, canceling notification")
            activeTasks.remove(taskId)
            notificationManager.cancel(NOTIFICATION_ID_BASE + taskId)
        }
        
        // Update notifications for all active tasks
        if (activeTasks.isNotEmpty()) {
            createTaskNotifications()
            if (updateJob?.isActive != true) {
                startTimerUpdates()
            }
        } else {
            println("SimpleTaskNotificationService: No active tasks remaining, stopping service")
            updateJob?.cancel()
            stopSelf()
        }
    }
    
    private fun createTaskNotifications() {
        activeTasks.values.forEach { task ->
            val notification = createTaskNotification(task)
            notificationManager.notify(NOTIFICATION_ID_BASE + task.id, notification)
        }
    }
    
    private fun createTaskNotification(task: TaskItem): Notification {
        // Open app intent
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this, task.id, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val formattedTime = formatTime(task.timeSeconds)
        val formattedHours = String.format("%.1f", task.timeHours)
        val taskColor = getTaskColor(task.color)
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(task.title)
            .setContentText(formattedTime)
            .setSubText("${formattedHours}h")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setColor(taskColor)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSilent(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$formattedTime\n${formattedHours} hours")
            )
            .build()
    }
    
    private fun startTimerUpdates() {
        updateJob?.cancel()
        updateJob = serviceScope.launch {
            while (activeTasks.isNotEmpty()) {
                delay(1000L)
                
                val updatedTasks = activeTasks.values.map { task ->
                    task.copy(
                        timeSeconds = task.timeSeconds + 1,
                        timeHours = (task.timeSeconds + 1) / 3600.0
                    )
                }
                
                updatedTasks.forEach { task ->
                    activeTasks[task.id] = task
                }
                
                withContext(Dispatchers.Main) {
                    createTaskNotifications()
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        updateJob?.cancel()
        // Cancel all task notifications
        activeTasks.keys.forEach { taskId ->
            notificationManager.cancel(NOTIFICATION_ID_BASE + taskId)
        }
    }
    
    private fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }
    
    private fun getTaskColor(color: TaskItem.TaskColor): Int {
        val composeColor = when (color) {
            TaskItem.TaskColor.DEFAULT -> Colors.TaskColors.Default
            TaskItem.TaskColor.BROWN -> Colors.TaskColors.Brown
            TaskItem.TaskColor.PINK -> Colors.TaskColors.Pink
            TaskItem.TaskColor.BLUE -> Colors.TaskColors.Blue
            TaskItem.TaskColor.BLACK -> Colors.TaskColors.Black
            TaskItem.TaskColor.ORANGE -> Colors.TaskColors.Orange
            TaskItem.TaskColor.LIME -> Colors.TaskColors.Lime
            TaskItem.TaskColor.TEAL -> Colors.TaskColors.Teal
        }
        return composeColorToAndroidColor(composeColor)
    }
    
    private fun composeColorToAndroidColor(composeColor: androidx.compose.ui.graphics.Color): Int {
        val red = (composeColor.red * 255).toInt()
        val green = (composeColor.green * 255).toInt()
        val blue = (composeColor.blue * 255).toInt()
        val alpha = (composeColor.alpha * 255).toInt()
        return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
    }
}
