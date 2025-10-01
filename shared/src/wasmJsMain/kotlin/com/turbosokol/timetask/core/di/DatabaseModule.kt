/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.core.di

import com.turbosokol.TimeTask.repository.datasource.InMemoryLocalTaskDataSource
import com.turbosokol.TimeTask.repository.datasource.LocalTaskDataSource
import org.koin.dsl.module

/**
 * WASM/JS-specific database module providing in-memory storage
 * Note: SQLDelight doesn't support WASM yet, so we use in-memory storage
 */
actual fun databaseModule() = module {
    // Use in-memory data source for WASM since SQLDelight doesn't support WASM yet
    single<LocalTaskDataSource> { InMemoryLocalTaskDataSource() }
}



