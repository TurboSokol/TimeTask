/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.turbosokol.TimeTask.di.initKoinForWasm
import com.turbosokol.TimeTask.ui.TaskListScreen
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Initialize Koin for WASM with in-memory storage
    initKoinForWasm()
    
    ComposeViewport(document.body!!) {
        TaskListScreen()
    }
}






