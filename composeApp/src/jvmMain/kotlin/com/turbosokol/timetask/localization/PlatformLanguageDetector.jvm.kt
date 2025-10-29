/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import java.util.Locale

/**
 * JVM implementation of platform language detector
 */
actual class PlatformLanguageDetector {
    
    actual fun getSystemLanguage(): Language {
        val locale = Locale.getDefault()
        return mapLocaleToLanguage(locale.language)
    }
    
    actual fun initialize() {
        // Set initial language based on system locale
        val currentLanguage = getSystemLanguage()
        LocalizationManager.setLanguage(currentLanguage)
    }
    
    actual fun cleanup() {
        // No cleanup needed for JVM
    }
}

/**
 * Composable function to set up JVM language detection
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
