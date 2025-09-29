/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.kmmreduxtemplate.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

/**
 * WASM/JS-specific database provider
 * Creates SQLDelight database with Web Worker driver
 */
object DatabaseProvider {
    
    suspend fun createDriver(): SqlDriver {
        return WebWorkerDriver(
            Worker(
                js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)""")
            )
        ).also { driver ->
            TaskDatabase.Schema.create(driver)
        }
    }
    
    suspend fun createDatabase(): TaskDatabase {
        val driver = createDriver()
        return TaskDatabase(driver)
    }
}

