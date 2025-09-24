package com.turbosokol.kmmreduxtemplate.core.network

import platform.Foundation.NSLog
import kotlin.time.ExperimentalTime

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

@ExperimentalTime
actual class LogServiceImpl actual constructor() : LogService, LogServiceBase() {
    
    /**
     * Log error messages using NSLog
     * Critical issues that should be investigated
     */
    actual override fun logError(message: String) {
        NSLog("[ERROR] KMMReduxTemplate: %@", message)
    }
    
    /**
     * Log warning messages using NSLog
     * Potential issues that should be monitored
     */
    actual override fun logWarning(message: String) {
        NSLog("[WARN] KMMReduxTemplate: %@", message)
    }
    
    /**
     * Log trace/debug messages using NSLog
     * Detailed information for debugging and monitoring
     */
    actual override fun logTrace(message: String) {
        NSLog("[TRACE] KMMReduxTemplate: %@", message)
    }
}

