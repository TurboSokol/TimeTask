package com.example.kmmreduxtemplate

import androidx.compose.ui.window.ComposeUIViewController
import com.example.kmmreduxtemplate.di.initKoinForCompose

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

fun MainViewController() = ComposeUIViewController { 
    // Initialize Koin for iOS
    initKoinForCompose()
    MainScreenWithBottomNavBar()
}