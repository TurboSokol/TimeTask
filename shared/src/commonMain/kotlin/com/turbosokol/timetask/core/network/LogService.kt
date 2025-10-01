package com.turbosokol.TimeTask.core.network

import com.turbosokol.TimeTask.core.redux.Action
import com.turbosokol.TimeTask.core.redux.Effect
import com.turbosokol.TimeTask.core.redux.Store
import com.turbosokol.TimeTask.core.redux.app.AppState
import io.ktor.client.plugins.logging.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue
import kotlin.time.ExperimentalTime

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

interface LogService {
    fun logError(message: String)
    fun logWarning(message: String)
    fun logTrace(message: String)
}

@ExperimentalTime
open class LogServiceBase : KoinComponent {
    protected val store: Store<AppState, Action, Effect> by inject()
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@ExperimentalTime
expect class LogServiceImpl constructor() : LogService, LogServiceBase {
    override fun logError(message: String)
    override fun logWarning(message: String)
    override fun logTrace(message: String)
}

@ExperimentalTime
class WebClientLogger(private val logService: LogService) : Logger {
    override fun log(message: String) {
        logService.logTrace(message)
    }
}