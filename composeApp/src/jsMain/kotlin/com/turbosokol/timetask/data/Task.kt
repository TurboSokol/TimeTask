/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.data

import kotlinx.datetime.Instant

/**
 * Simple task data class for WASM in-memory storage
 */
data class Task(
    val id: Int,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant
)







