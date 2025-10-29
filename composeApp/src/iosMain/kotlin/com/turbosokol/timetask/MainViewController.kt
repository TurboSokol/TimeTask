package com.turbosokol.TimeTask

import androidx.compose.ui.window.ComposeUIViewController
import com.turbosokol.TimeTask.di.initKoinForCompose

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

// Initialize Koin once at module level for iOS
private val isKoinInitialized by lazy {
    initKoinForCompose()
    true
}

fun MainViewController() = ComposeUIViewController { 
    // Ensure Koin is initialized before UI
    isKoinInitialized
    MainScreenWithBottomNavBar()
}