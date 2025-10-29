/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.localization

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import java.util.Locale

/**
 * Android implementation of platform language detector
 */
actual class PlatformLanguageDetector {
    private var context: Context? = null
    var lifecycleObserver: LifecycleEventObserver? = null
    
    actual fun getSystemLanguage(): Language {
        val context = this.context ?: return Language.ENGLISH
        
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
        
        return mapLocaleToLanguage(locale.language)
    }
    
    actual fun initialize() {
        // Android initialization is handled in the Composable
    }
    
    actual fun cleanup() {
        lifecycleObserver = null
        context = null
    }
    
    /**
     * Set the context for language detection
     */
    fun setContext(context: Context) {
        this.context = context
    }
    
    /**
     * Handle configuration changes
     */
    fun onConfigurationChanged(newConfig: Configuration) {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            newConfig.locales[0]
        } else {
            @Suppress("DEPRECATION")
            newConfig.locale
        }
        
        val newLanguage = mapLocaleToLanguage(locale.language)
        LocalizationManager.setLanguage(newLanguage)
    }
}

/**
 * Composable function to set up Android language detection
 */
@Composable
actual fun rememberPlatformLanguageDetector(): PlatformLanguageDetector {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val detector = remember { PlatformLanguageDetector() }
    
    DisposableEffect(detector, lifecycleOwner) {
        detector.setContext(context)
        
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    // Check for language changes when app resumes
                    val currentLanguage = detector.getSystemLanguage()
                    LocalizationManager.setLanguage(currentLanguage)
                }
                else -> {}
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        detector.lifecycleObserver = observer
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            detector.cleanup()
        }
    }
    
    return detector
}
