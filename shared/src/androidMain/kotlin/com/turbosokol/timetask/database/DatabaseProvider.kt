/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.database

import android.content.Context
import android.util.Log
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

/**
 * Android-specific database provider
 * Creates SQLDelight database with Android SQLite driver
 */
object DatabaseProvider {
    
    private const val TAG = "DatabaseProvider"
    private var _database: TaskDatabase? = null
    private var _isInitialized = false
    
    /**
     * Initialize database at app start with comprehensive checks
     */
    fun initializeDatabase(context: Context): TaskDatabase {
        if (!_isInitialized) {
            Log.d(TAG, "Initializing Android database...")
            val driver = createAndInitializeDriver(context)
            _database = TaskDatabase(driver)
            _isInitialized = true
            Log.d(TAG, "Android database initialized successfully")
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
    
    private fun createAndInitializeDriver(context: Context): SqlDriver {
        Log.d(TAG, "Creating Android SQLite driver...")

//        // Clear any existing database to avoid schema conflicts
//        val databaseFile = context.getDatabasePath("task_database.db")
//        if (databaseFile.exists()) {
//            Log.d(TAG, "Clearing existing database to avoid schema conflicts...")
//            databaseFile.delete()
//        }

        // AndroidSqliteDriver automatically handles schema creation and persistence
        val driver: SqlDriver = AndroidSqliteDriver(TaskDatabase.Schema, context, "task_database.db")
        
        Log.d(TAG, "Android SQLite driver created successfully")
        
        return driver
    }
    
    /**
     * Legacy method for backward compatibility
     */
    @Deprecated("Use initializeDatabase() instead")
    fun createDatabase(context: Context): TaskDatabase {
        return initializeDatabase(context)
    }
}


