/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSLocale
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationWillEnterForegroundNotification
import platform.darwin.NSObjectProtocol

/**
 * iOS implementation of platform language detector
 */
actual class PlatformLanguageDetector {
    private var notificationObserver: NSObjectProtocol? = null
    
    actual fun getSystemLanguage(): Language {
        return try {
            // Get system language from NSUserDefaults
            val userDefaults = NSUserDefaults.standardUserDefaults
            val languages = userDefaults.objectForKey("AppleLanguages") as? List<*>
            
            if (languages != null && languages.isNotEmpty()) {
                val primaryLanguage = languages.first() as? String
                if (primaryLanguage != null) {
                    // Extract language code (e.g., "ru" from "ru-RU")
                    val languageCode = if (primaryLanguage.contains("-")) {
                        primaryLanguage.substring(0, primaryLanguage.indexOf("-"))
                    } else {
                        primaryLanguage
                    }
                    mapLocaleToLanguage(languageCode)
                } else {
                    Language.ENGLISH
                }
            } else {
                Language.ENGLISH
            }
        } catch (e: Exception) {
            Language.ENGLISH
        }
    }
    
    actual fun initialize() {
        // Set up notification observer for language changes
        setupNotificationObserver()
        
        // Set initial language
        val currentLanguage = getSystemLanguage()
        LocalizationManager.setLanguage(currentLanguage)
    }
    
    actual fun cleanup() {
        notificationObserver?.let { observer ->
            NSNotificationCenter.defaultCenter.removeObserver(observer)
        }
        notificationObserver = null
    }
    
    private fun setupNotificationObserver() {
        // Observe app entering foreground to check for language changes
        val foregroundObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIApplicationWillEnterForegroundNotification,
            `object` = null,
            queue = null
        ) { _ ->
            // Check for language changes when app enters foreground
            val currentLanguage = getSystemLanguage()
            LocalizationManager.setLanguage(currentLanguage)
        }
        
        // Store the observer for cleanup
        notificationObserver = foregroundObserver
    }
}

/**
 * Composable function to set up iOS language detection
 */
@OptIn(ExperimentalForeignApi::class)
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
