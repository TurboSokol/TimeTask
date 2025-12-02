/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.turbosokol.TimeTask.service.TimerUpdateService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Broadcast receiver that handles timer alarm events
 * Updates active tasks and schedules the next alarm
 */
class TaskTimerAlarmReceiver : BroadcastReceiver() {
    
    companion object {
        const val KEY_ACTIVE_TASK_IDS = "active_task_ids"
        const val KEY_UPDATE_INTERVAL_MS = "update_interval_ms"
    }
    
    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onReceive(context: Context, intent: Intent) {
        println("TaskTimerAlarmReceiver: Alarm received")
        
        val activeTaskIds = intent.getIntArrayExtra(KEY_ACTIVE_TASK_IDS)?.toList() ?: emptyList()
        val updateIntervalMs = intent.getLongExtra(KEY_UPDATE_INTERVAL_MS, 60000L)
        
        if (activeTaskIds.isEmpty()) {
            println("TaskTimerAlarmReceiver: No active tasks, completing alarm")
            return
        }
        
        println("TaskTimerAlarmReceiver: Processing ${activeTaskIds.size} active tasks with interval ${updateIntervalMs}ms")
        
        // Process timer updates in background scope
        receiverScope.launch {
            try {
                // Update active tasks with elapsed time
                TimerUpdateService.updateActiveTasks(context, activeTaskIds, updateIntervalMs)
                
                // Schedule next alarm
                TaskTimerAlarmManager.scheduleNextTimerAlarm(
                    context,
                    activeTaskIds,
                    updateIntervalMs
                )
                
                println("TaskTimerAlarmReceiver: Successfully processed timer update and scheduled next alarm")
            } catch (e: Exception) {
                println("TaskTimerAlarmReceiver: Error processing timer update: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
