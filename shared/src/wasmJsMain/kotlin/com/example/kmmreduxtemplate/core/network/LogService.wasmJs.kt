package com.example.kmmreduxtemplate.core.network

import kotlin.time.ExperimentalTime

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

@ExperimentalTime  
actual class LogServiceImpl actual constructor() : LogService, LogServiceBase() {
    
    /**
     * Log error messages using println
     * Critical issues that should be investigated
     */
    actual override fun logError(message: String) {
        println("[ERROR] KMMReduxTemplate: $message")
    }
    
    /**
     * Log warning messages using println
     * Potential issues that should be monitored
     */
    actual override fun logWarning(message: String) {
        println("[WARN] KMMReduxTemplate: $message")
    }
    
    /**
     * Log trace/debug messages using println
     * Detailed information for debugging and monitoring
     */
    actual override fun logTrace(message: String) {
        println("[TRACE] KMMReduxTemplate: $message")
    }
}
