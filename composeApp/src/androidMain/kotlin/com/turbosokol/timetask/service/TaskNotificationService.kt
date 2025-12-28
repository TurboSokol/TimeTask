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
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.turbosokol.TimeTask.MainActivity
import com.turbosokol.TimeTask.core.redux.Action
import com.turbosokol.TimeTask.core.redux.Effect
import com.turbosokol.TimeTask.core.redux.Store
import com.turbosokol.TimeTask.core.redux.app.AppState
import com.turbosokol.TimeTask.data.TaskItemParcelable
import com.turbosokol.TimeTask.database.DatabaseProvider
import com.turbosokol.TimeTask.repository.TaskRepositoryImpl
import com.turbosokol.TimeTask.repository.datasource.SqlDelightLocalTaskDataSource
import com.turbosokol.TimeTask.screensStates.HomeScreenAction
import com.turbosokol.TimeTask.screensStates.TaskItem
import com.turbosokol.TimeTask.values.Colors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Foreground service that manages all timer operations for active tasks.
 * Handles timer updates, notifications, database persistence, and Redux state updates.
 * This is the single source of truth for timer processing.
 *
 * Uses FOREGROUND_SERVICE_TYPE_SPECIAL_USE to support long-running timer sessions (6+ hours).
 * This type is not subject to the 6-hour daily limit imposed on dataSync services in Android 15+.
 *
 * For optimal stability across different device manufacturers:
 * - Service runs with START_STICKY to restart after system kills
 * - User should disable battery optimization for the app
 * - WAKE_LOCK permission ensures timer continues when device sleeps
 */
class TaskNotificationService : Service(), KoinComponent {

    companion object Companion {
        private const val NOTIFICATION_ID_BASE = 1000
        private const val CHANNEL_ID = "task_timer_channel"
        private const val CHANNEL_NAME = "Task Timer Notifications"
        private const val CHANNEL_DESCRIPTION =
            "Shows notifications for active task timers with real-time updates"
        private const val TIMER_UPDATE_INTERVAL_MS = 1000L // Update every second

        // Actions
        private const val ACTION_OPEN_APP = "open_app"
        private const val ACTION_UPDATE_TASKS = "UPDATE_TASKS"

        /**
         * Start the foreground service with the given tasks
         */
        fun startService(context: Context, tasks: List<TaskItem>) {
            val intent = Intent(context, TaskNotificationService::class.java)
            intent.putParcelableArrayListExtra("tasks", ArrayList(tasks.map { task ->
                TaskItemParcelable(
                    id = task.id,
                    title = task.title,
                    isActive = task.isActive,
                    startTimeStamp = task.startTimeStamp,
                    overallTime = task.overallTime,
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

        /**
         * Stop the foreground service
         */
        fun stopService(context: Context) {
            val intent = Intent(context, TaskNotificationService::class.java)
            context.stopService(intent)
        }

        /**
         * Update tasks in the running service
         */
        fun updateTasks(context: Context, tasks: List<TaskItem>) {
            val intent = Intent(context, TaskNotificationService::class.java)
            intent.action = ACTION_UPDATE_TASKS
            intent.putParcelableArrayListExtra("tasks", ArrayList(tasks.map { task ->
                TaskItemParcelable(
                    id = task.id,
                    title = task.title,
                    isActive = task.isActive,
                    startTimeStamp = task.startTimeStamp,
                    overallTime = task.overallTime,
                    timeSeconds = task.timeSeconds,
                    timeHours = task.timeHours,
                    color = task.color.name
                )
            }))
            context.startService(intent)
        }
    }

    // Access Store through Koin
    private val store: Store<AppState, Action, Effect> by inject()

    private lateinit var notificationManager: NotificationManager
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var timerJob: Job? = null

    // Active tasks being tracked by this service
    private val activeTasks = mutableMapOf<Int, TaskItem>()

    // Repository for database operations
    private lateinit var taskRepository: TaskRepositoryImpl

    override fun onCreate() {
        super.onCreate()
        println("SimpleTaskNotificationService: onCreate")
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()

        // Initialize repository
        val database = DatabaseProvider.initializeDatabase(this)
        val localDataSource = SqlDelightLocalTaskDataSource(database)
        taskRepository = TaskRepositoryImpl(localDataSource, Dispatchers.IO)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("SimpleTaskNotificationService: onStartCommand called with action: ${intent?.action}")

        when (intent?.action) {
            ACTION_UPDATE_TASKS -> {
                val tasks = parseTasksFromIntent(intent)
                updateActiveTasks(tasks)
            }

            ACTION_OPEN_APP -> {
                val openAppIntent = Intent(this, MainActivity::class.java)
                openAppIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(openAppIntent)
            }

            else -> {
                val tasks = parseTasksFromIntent(intent)
                startForegroundWithTasks(tasks)
            }
        }

        return START_STICKY
    }

    private fun parseTasksFromIntent(intent: Intent?): List<TaskItem> {
        return intent?.getParcelableArrayListExtra<TaskItemParcelable>("tasks")?.map { parcelable ->
            TaskItem(
                id = parcelable.id,
                title = parcelable.title,
                isActive = parcelable.isActive,
                startTimeStamp = parcelable.startTimeStamp,
                overallTime = parcelable.overallTime,
                timeSeconds = parcelable.timeSeconds,
                timeHours = parcelable.timeHours,
                color = try {
                    TaskItem.TaskColor.valueOf(parcelable.color)
                } catch (e: IllegalArgumentException) {
                    TaskItem.TaskColor.DEFAULT
                }
            )
        } ?: emptyList()
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

    private fun startForegroundWithTasks(tasks: List<TaskItem>) {
        println("SimpleTaskNotificationService: startForegroundWithTasks called with ${tasks.size} tasks")

        activeTasks.clear()
        tasks.forEach { task ->
            activeTasks[task.id] = task
        }

        println("SimpleTaskNotificationService: Processing ${activeTasks.size} active tasks")

        if (activeTasks.isNotEmpty()) {
            // Start foreground service with the first task's notification
            val firstTask = activeTasks.values.first()
            val mainNotification = createTaskNotification(firstTask)

            // Start foreground with proper type for Android 14+ (specialUse for long-running timer)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NOTIFICATION_ID_BASE + firstTask.id,
                    mainNotification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } else {
                startForeground(NOTIFICATION_ID_BASE + firstTask.id, mainNotification)
            }

            // Create notifications for all active tasks
            updateAllNotifications()

            // Start timer loop
            startTimerLoop()

            println("SimpleTaskNotificationService: Started foreground service with ${activeTasks.size} tasks")
        } else {
            println("SimpleTaskNotificationService: No active tasks, stopping service")
            stopSelf()
        }
    }

    private fun updateActiveTasks(tasks: List<TaskItem>) {
        val currentActiveIds = activeTasks.keys.toSet()
        val newActiveIds = tasks.map { it.id }.toSet()

        println("SimpleTaskNotificationService: updateActiveTasks - Current: $currentActiveIds, New: $newActiveIds")

        // Update existing tasks with new data
        tasks.forEach { task ->
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
            updateAllNotifications()

            // Ensure timer is running
            if (timerJob?.isActive != true) {
                startTimerLoop()
            }
        } else {
            println("SimpleTaskNotificationService: No active tasks remaining, stopping service")
            stopTimerLoop()
            stopSelf()
        }
    }

    private fun updateAllNotifications() {
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

    /**
     * Starts the main timer loop that updates all active tasks every second.
     * This is the central timer processing mechanism for the foreground service.
     */
    private fun startTimerLoop() {
        // Cancel any existing timer
        timerJob?.cancel()

        println("SimpleTaskNotificationService: Starting timer loop for ${activeTasks.size} tasks")

        timerJob = serviceScope.launch {
            while (activeTasks.isNotEmpty()) {
                delay(TIMER_UPDATE_INTERVAL_MS)

                val currentTime = Clock.System.now().epochSeconds
                val updatedTasks = mutableListOf<TaskItem>()

                // Update each active task
                activeTasks.values.toList().forEach { task ->
                    if (task.isActive && task.startTimeStamp != 0L) {
                        // Calculate elapsed time since start
                        val actualSeconds = (currentTime - task.startTimeStamp) + task.overallTime
                        val newTimeHours = actualSeconds / 3600.0

                        val updatedTask = task.copy(
                            timeSeconds = actualSeconds,
                            timeHours = newTimeHours
                        )

                        // Update in memory
                        activeTasks[task.id] = updatedTask
                        updatedTasks.add(updatedTask)
                    }
                }

                if (updatedTasks.isNotEmpty()) {
                    // Update notifications
                    updateAllNotifications()

                    // Persist to database (async)
                    launch(Dispatchers.IO) {
                        updatedTasks.forEach { task ->
                            try {
                                taskRepository.updateTask(task)
                                println("SimpleTaskNotificationService: Updated task ${task.id} in DB: ${task.timeSeconds}s")
                            } catch (e: Exception) {
                                println("SimpleTaskNotificationService: Failed to update task ${task.id} in DB: ${e.message}")
                            }
                        }
                    }

                    // Update Redux state directly through Store
                    updatedTasks.forEach { task ->
                        store.dispatch(HomeScreenAction.TaskUpdated(task))
                    }

                }
            }

            println("SimpleTaskNotificationService: Timer loop ended - no active tasks")
        }
    }

    private fun stopTimerLoop() {
        println("SimpleTaskNotificationService: Stopping timer loop")
        timerJob?.cancel()
        timerJob = null
    }

    override fun onDestroy() {
        super.onDestroy()
        println("SimpleTaskNotificationService: onDestroy")

        // Stop timer loop
        stopTimerLoop()

        // Cancel all task notifications
        activeTasks.keys.forEach { taskId ->
            notificationManager.cancel(NOTIFICATION_ID_BASE + taskId)
        }

        activeTasks.clear()
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
