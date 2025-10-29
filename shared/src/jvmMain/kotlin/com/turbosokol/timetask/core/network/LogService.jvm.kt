@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.turbosokol.TimeTask.core.network

import kotlin.time.ExperimentalTime

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/


@ExperimentalTime
actual class LogServiceImpl actual constructor() : LogService, LogServiceBase() {
    
    /**
     * Log error messages to System.err
     * Critical issues that should be investigated
     */
    actual override fun logError(message: String) {
        System.err.println("[ERROR] KMMReduxTemplate: $message")
    }
    
    /**
     * Log warning messages to System.out
     * Potential issues that should be monitored  
     */
    actual override fun logWarning(message: String) {
        System.out.println("[WARN] KMMReduxTemplate: $message")
    }
    
    /**
     * Log trace/debug messages to System.out
     * Detailed information for debugging and monitoring
     */
    actual override fun logTrace(message: String) {
        System.out.println("[TRACE] KMMReduxTemplate: $message")
    }
}

