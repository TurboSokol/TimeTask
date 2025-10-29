/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import org.w3c.dom.window

/**
 * JS implementation of platform language detector
 */
actual class PlatformLanguageDetector {
    
    actual fun getSystemLanguage(): Language {
        return try {
            val language = window.navigator.language
            mapLocaleToLanguage(language)
        } catch (e: Exception) {
            Language.ENGLISH
        }
    }
    
    actual fun initialize() {
        // Set initial language based on browser locale
        val currentLanguage = getSystemLanguage()
        LocalizationManager.setLanguage(currentLanguage)
    }
    
    actual fun cleanup() {
        // No cleanup needed for JS
    }
}

/**
 * Composable function to set up JS language detection
 */
@Composable
actual fun rememberPlatformLanguageDetector(): PlatformLanguageDetector {
    val detector = remember { PlatformLanguageDetector() }
    
    DisposableEffect(detector) {
        detector.initialize()
        
        onDispose {
            detector.cleanup()
        }
    }
    
    return detector
}
