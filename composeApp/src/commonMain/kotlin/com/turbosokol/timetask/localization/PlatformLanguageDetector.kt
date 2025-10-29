/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.localization

import androidx.compose.runtime.Composable

/**
 * Common interface for platform-specific language detection
 */
expect class PlatformLanguageDetector {
    /**
     * Get the current system language
     * @return Language enum based on system locale
     */
    fun getSystemLanguage(): Language
    
    /**
     * Initialize language detection and set up listeners for configuration changes
     */
    fun initialize()
    
    /**
     * Clean up resources when no longer needed
     */
    fun cleanup()
}

/**
 * Utility function to map locale strings to our Language enum
 */
fun mapLocaleToLanguage(locale: String): Language {
    return when {
        locale.startsWith("ru") -> Language.RUSSIAN
        locale.startsWith("en") -> Language.ENGLISH
        else -> Language.ENGLISH // Default to English
    }
}

/**
 * Composable function to set up platform language detection
 */
@Composable
expect fun rememberPlatformLanguageDetector(): PlatformLanguageDetector
