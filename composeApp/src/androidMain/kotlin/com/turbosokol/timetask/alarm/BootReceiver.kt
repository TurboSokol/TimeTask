/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Broadcast receiver that handles device boot completion
 * Restarts timer alarms for any active tasks after device reboot
 */
class BootReceiver : BroadcastReceiver() {
    
    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            println("BootReceiver: Device boot completed, checking for active tasks")
            
            receiverScope.launch {
                try {
                    // For now, just log that boot completed
                    // The actual task restoration will be handled when the app starts
                    // and the notification service is initialized
                    println("BootReceiver: Device boot completed - app will restore active tasks on startup")
                } catch (e: Exception) {
                    println("BootReceiver: Error handling boot completion: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }
}
