/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/**
 * iOS-specific database provider
 * Creates SQLDelight database with native SQLite driver
 */
object DatabaseProvider {
    
    private var _database: TaskDatabase? = null
    private var _isInitialized = false
    
    /**
     * Initialize database at app start with comprehensive checks
     */
    fun initializeDatabase(): TaskDatabase {
        if (!_isInitialized) {
            println("Initializing iOS database...")
            val driver = createAndInitializeDriver()
            _database = TaskDatabase(driver)
            _isInitialized = true
            println("iOS database initialized successfully")
        }
        return _database!!
    }
    
    /**
     * Get existing database instance (must be initialized first)
     */
    fun getDatabase(): TaskDatabase {
        if (!_isInitialized) {
            throw IllegalStateException("Database not initialized. Call initializeDatabase() first.")
        }
        return _database!!
    }
    
    private fun createAndInitializeDriver(): SqlDriver {
        println("Creating iOS SQLite driver...")
        
        // NativeSqliteDriver automatically handles schema creation and persistence
        // It will store the database in the app's Documents directory
        val driver: SqlDriver = NativeSqliteDriver(TaskDatabase.Schema, "task_database.db")
        
        println("iOS SQLite driver created successfully")
        
        return driver
    }
    
    /**
     * Legacy method for backward compatibility
     */
    @Deprecated("Use initializeDatabase() instead")
    fun createDatabase(): TaskDatabase {
        return initializeDatabase()
    }
}