/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.alarm

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import com.turbosokol.TimeTask.screensStates.TaskItem

/**
 * Helper class for testing AlarmManager functionality
 * Provides utilities to verify alarm scheduling and execution
 */
object AlarmManagerTestHelper {
    
    /**
     * Test if AlarmManager is properly configured and can schedule alarms
     */
    fun testAlarmScheduling(context: Context): Boolean {
        return try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            
            // Test basic alarm scheduling capability
            val testIntent = Intent(context, TaskTimerAlarmReceiver::class.java).apply {
                putExtra(TaskTimerAlarmReceiver.KEY_ACTIVE_TASK_IDS, intArrayOf(999)) // Test task ID
                putExtra(TaskTimerAlarmReceiver.KEY_UPDATE_INTERVAL_MS, 5000L) // 5 second test interval
            }
            
            val pendingIntent = android.app.PendingIntent.getBroadcast(
                context,
                9999, // Test request code
                testIntent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )
            
            val triggerTime = SystemClock.elapsedRealtime() + 5000L
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
            
            // Cancel the test alarm immediately
            alarmManager.cancel(pendingIntent)
            
            println("AlarmManagerTestHelper: Alarm scheduling test passed")
            true
        } catch (e: SecurityException) {
            println("AlarmManagerTestHelper: Alarm scheduling test failed - ${e.message}")
            false
        } catch (e: Exception) {
            println("AlarmManagerTestHelper: Alarm scheduling test failed - ${e.message}")
            false
        }
    }
    
    /**
     * Test if exact alarm permission is available (Android 12+)
     */
    fun testExactAlarmPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true // No restriction on older versions
        }
    }
    
    /**
     * Create a test task for alarm testing
     */
    fun createTestTask(id: Int = 999): TaskItem {
        return TaskItem(
            id = id,
            title = "Test Task",
            isActive = true,
            timeSeconds = 0L,
            timeHours = 0.0,
            color = TaskItem.TaskColor.DEFAULT
        )
    }
    
    /**
     * Log current alarm status for debugging
     */
    fun logAlarmStatus(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        
        println("AlarmManagerTestHelper: Alarm status:")
        println("  - Can schedule exact alarms: ${testExactAlarmPermission(context)}")
        println("  - Android version: ${Build.VERSION.SDK_INT}")
        println("  - Alarm manager available: ${alarmManager != null}")
    }
}
