package com.turbosokol.kmmreduxtemplate

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.turbosokol.kmmreduxtemplate.di.initKoinForCompose
import kotlinx.browser.document

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Initialize Koin for WASM/JS
    initKoinForCompose()
    
    ComposeViewport(document.body!!) {
        MainScreenWithBottomNavBar()
    }
}