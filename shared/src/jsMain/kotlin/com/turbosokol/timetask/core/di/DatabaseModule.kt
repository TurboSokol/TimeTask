/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.core.di

import com.turbosokol.TimeTask.repository.datasource.InMemoryTaskDataSource
import com.turbosokol.TimeTask.repository.datasource.LocalTaskDataSource
import org.koin.dsl.module

/**
 * JS-specific database module using in-memory storage
 */
actual fun databaseModule() = module {
    // In-memory data source for JS (SQLDelight JS driver not yet implemented)
    single<LocalTaskDataSource> { InMemoryTaskDataSource() }
}



