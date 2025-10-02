/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.di

import com.turbosokol.TimeTask.repository.TaskRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Simple Koin configuration for WASM with in-memory storage
 */
fun initKoinForWasm() {
    startKoin {
        modules(
            wasmModule
        )
    }
}

private val wasmModule = module {
    single<TaskRepository> { TaskRepository() }
}








