/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.kmmreduxtemplate.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

/**
 * Android-specific database provider
 * Creates SQLDelight database with Android SQLite driver
 */
object DatabaseProvider {
    
    fun createDriver(context: Context): SqlDriver {
        return AndroidSqliteDriver(
            schema = TaskDatabase.Schema,
            context = context,
            name = "task_database.db"
        )
    }
    
    fun createDatabase(context: Context): TaskDatabase {
        val driver = createDriver(context)
        return TaskDatabase(driver)
    }
}

