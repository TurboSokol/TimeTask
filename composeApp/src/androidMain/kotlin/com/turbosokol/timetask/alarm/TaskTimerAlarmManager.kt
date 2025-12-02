/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock

/**
 * Manages AlarmManager scheduling for task timer updates
 * Handles background timer processing when app is not active or device is powered off
 */
object TaskTimerAlarmManager {
    
    private const val ALARM_REQUEST_CODE = 1001
    private const val DEFAULT_UPDATE_INTERVAL = 1000L // 1 second in milliseconds for real-time updates
    
    /**
     * Start timer alarm for active tasks
     * Schedules periodic updates that persist timer state to database
     */
    fun startTimerAlarm(
        context: Context,
        activeTaskIds: List<Int>,
        updateIntervalMs: Long = DEFAULT_UPDATE_INTERVAL
    ) {
        if (activeTaskIds.isEmpty()) {
            println("TaskTimerAlarmManager: No active tasks, not starting alarm")
            return
        }
        
        println("TaskTimerAlarmManager: Starting timer alarm for ${activeTaskIds.size} tasks with ${updateIntervalMs}ms interval")
        
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TaskTimerAlarmReceiver::class.java).apply {
            putExtra(TaskTimerAlarmReceiver.KEY_ACTIVE_TASK_IDS, activeTaskIds.toIntArray())
            putExtra(TaskTimerAlarmReceiver.KEY_UPDATE_INTERVAL_MS, updateIntervalMs)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val triggerTime = SystemClock.elapsedRealtime() + updateIntervalMs
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
            println("TaskTimerAlarmManager: Timer alarm scheduled successfully")
        } catch (e: SecurityException) {
            println("TaskTimerAlarmManager: Failed to schedule alarm - ${e.message}")
        }
    }
    
    /**
     * Schedule next timer alarm
     * Called by TaskTimerAlarmReceiver to continue the timer chain
     */
    fun scheduleNextTimerAlarm(
        context: Context,
        activeTaskIds: List<Int>,
        updateIntervalMs: Long = DEFAULT_UPDATE_INTERVAL
    ) {
        if (activeTaskIds.isEmpty()) {
            stopTimerAlarm(context)
            return
        }
        
        println("TaskTimerAlarmManager: Scheduling next timer alarm for ${activeTaskIds.size} tasks")
        startTimerAlarm(context, activeTaskIds, updateIntervalMs)
    }
    
    /**
     * Stop timer alarm
     * Cancels any pending timer alarms
     */
    fun stopTimerAlarm(context: Context) {
        println("TaskTimerAlarmManager: Stopping timer alarm")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TaskTimerAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        try {
            alarmManager.cancel(pendingIntent)
            println("TaskTimerAlarmManager: Timer alarm cancelled successfully")
        } catch (e: SecurityException) {
            println("TaskTimerAlarmManager: Failed to cancel alarm - ${e.message}")
        }
    }
    
    /**
     * Update timer alarm with new active tasks
     * Replaces existing alarm with updated task list
     */
    fun updateTimerAlarm(
        context: Context,
        activeTaskIds: List<Int>,
        updateIntervalMs: Long = DEFAULT_UPDATE_INTERVAL
    ) {
        if (activeTaskIds.isEmpty()) {
            stopTimerAlarm(context)
        } else {
            startTimerAlarm(context, activeTaskIds, updateIntervalMs)
        }
    }
}
