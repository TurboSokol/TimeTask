/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

/**
 * JVM-specific database provider
 * Creates SQLDelight database with SQLite driver
 */
object DatabaseProvider {
    
    private var _database: TaskDatabase? = null
    private var _isInitialized = false
    
    /**
     * Initialize database at app start with comprehensive checks
     */
    fun initializeDatabase(): TaskDatabase {
        if (!_isInitialized) {
            println("DatabaseProvider: Initializing database...")
            val driver = createAndInitializeDriver()
            _database = TaskDatabase(driver)
            _isInitialized = true
            println("DatabaseProvider: Database initialized successfully")
        } else {
            println("DatabaseProvider: Database already initialized, returning existing instance")
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
        // Use user home directory for persistent storage
        val userHome = System.getProperty("user.home")
        val appDataDir = File(userHome, ".timetask")
        val databasePath = File(appDataDir, "task_database.db")
        
        // Ensure parent directory exists
        if (!appDataDir.exists()) {
            appDataDir.mkdirs()
            println("Created database directory: ${appDataDir.absolutePath}")
        }
        
        println("Database path: ${databasePath.absolutePath}")
        println("Database file exists: ${databasePath.exists()}")
        
        val driver = JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}")
        
        // Initialize database schema
        initializeDatabaseSchema(driver)
        
        return driver
    }
    
    private fun initializeDatabaseSchema(driver: SqlDriver) {
        try {
            // Create database schema (SQLDelight handles existing tables gracefully)
            createDatabaseSchema(driver)
            println("Database schema initialized successfully")
        } catch (e: Exception) {
            // If table already exists, that's fine - just log and continue
            if (e.message?.contains("already exists") == true) {
                println("Database tables already exist, continuing...")
            } else {
                println("Error initializing database schema: ${e.message}")
                e.printStackTrace()
                throw RuntimeException("Failed to initialize database", e)
            }
        }
    }
    
    private fun createDatabaseSchema(driver: SqlDriver) {
        try {
            TaskDatabase.Schema.create(driver)
            println("Schema creation completed")
        } catch (e: Exception) {
            println("Error creating schema: ${e.message}")
            throw e
        }
    }
    
    /**
     * Legacy method for backward compatibility
     */
    @Deprecated("Use initializeDatabase() instead")
    fun createDatabase(): TaskDatabase {
        return initializeDatabase()
    }
}


