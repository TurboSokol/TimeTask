/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.kmmreduxtemplate.core.di

import com.turbosokol.kmmreduxtemplate.database.DatabaseProvider
import com.turbosokol.kmmreduxtemplate.database.TaskDatabase
import org.koin.dsl.module

/**
 * JVM-specific database module providing SQLDelight database instance
 */
actual fun databaseModule() = module {
    single<TaskDatabase> { 
        DatabaseProvider.createDatabase()
    }
}


