/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.kmmreduxtemplate.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/**
 * iOS-specific database provider
 * Creates SQLDelight database with Native SQLite driver
 */
object DatabaseProvider {
    
    fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = TaskDatabase.Schema,
            name = "task_database.db"
        )
    }
    
    fun createDatabase(): TaskDatabase {
        val driver = createDriver()
        return TaskDatabase(driver)
    }
}

