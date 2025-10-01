/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.core.di

import com.turbosokol.TimeTask.database.DatabaseProvider
import com.turbosokol.TimeTask.database.TaskDatabase
import com.turbosokol.TimeTask.repository.datasource.LocalTaskDataSource
import com.turbosokol.TimeTask.repository.datasource.SqlDelightLocalTaskDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific database module providing SQLDelight database instance
 */
actual fun databaseModule() = module {
    single<TaskDatabase> { 
        DatabaseProvider.initializeDatabase(androidContext())
    }
    
    single<LocalTaskDataSource> { 
        SqlDelightLocalTaskDataSource(get())
    }
}
