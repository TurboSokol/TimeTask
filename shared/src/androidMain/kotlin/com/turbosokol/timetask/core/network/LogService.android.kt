package com.turbosokol.TimeTask.core.network

import android.util.Log
import kotlin.time.ExperimentalTime

/***
 * Android-specific LogService implementation using Android Log API
 * Leverages Log.d/w/e for proper Android logging with tag support
 ***/

@ExperimentalTime
actual class LogServiceImpl actual constructor() : LogService, LogServiceBase() {
    
    companion object {
        private const val TAG = "KMMReduxTemplate"
    }
    
    /**
     * Log error messages using Android Log.e
     * Critical issues that should be investigated
     */
    actual override fun logError(message: String) {
        Log.e(TAG, message)
    }
    
    /**
     * Log warning messages using Android Log.w  
     * Potential issues that should be monitored
     */
    actual override fun logWarning(message: String) {
        Log.w(TAG, message)
    }
    
    /**
     * Log trace/debug messages using Android Log.d
     * Detailed information for debugging and monitoring
     */
    actual override fun logTrace(message: String) {
        Log.d(TAG, message)
    }
}

