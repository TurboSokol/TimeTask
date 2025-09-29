/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.kmmreduxtemplate.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.sqlite.SQLiteDriver
import java.io.File

/**
 * JVM-specific database provider
 * Creates SQLDelight database with SQLite driver
 */
object DatabaseProvider {
    
    fun createDriver(): SqlDriver {
        val databasePath = File(System.getProperty("java.io.tmpdir"), "task_database.db")
        val driver = SQLiteDriver("jdbc:sqlite:${databasePath.absolutePath}")
        
        // Create tables if they don't exist
        TaskDatabase.Schema.create(driver)
        
        return driver
    }
    
    fun createDatabase(): TaskDatabase {
        val driver = createDriver()
        return TaskDatabase(driver)
    }
}
