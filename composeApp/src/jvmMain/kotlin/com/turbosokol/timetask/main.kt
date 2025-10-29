package com.turbosokol.TimeTask

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.turbosokol.TimeTask.di.initKoinForCompose

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

fun main() {
    // Initialize Koin for JVM
    initKoinForCompose()
    
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "KMMREDUXTemplate",
        ) {
            MainScreenWithBottomNavBar()
        }
    }
}