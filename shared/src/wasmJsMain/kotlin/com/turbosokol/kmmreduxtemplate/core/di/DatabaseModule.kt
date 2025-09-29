/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.kmmreduxtemplate.core.di

import com.turbosokol.kmmreduxtemplate.database.DatabaseProvider
import com.turbosokol.kmmreduxtemplate.database.TaskDatabase
import org.koin.dsl.module

/**
 * WASM/JS-specific database module providing SQLDelight database instance
 */
actual fun databaseModule() = module {
    single<TaskDatabase> { 
        // Note: WASM database creation is async, but Koin doesn't support suspend factories
        // In production, consider initializing database separately before starting Koin
        throw NotImplementedError("WASM database initialization needs special handling due to async nature")
    }
}


