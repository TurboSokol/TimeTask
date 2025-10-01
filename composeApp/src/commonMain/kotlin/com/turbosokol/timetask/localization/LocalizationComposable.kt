/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Composable hook to reactively get localized strings
 */
@Composable
fun rememberLocalizedString(key: String): String {
    var currentLanguage by remember { mutableStateOf(LocalizationManager.getCurrentLanguage()) }
    
    DisposableEffect(Unit) {
        val listener: (Language) -> Unit = { newLanguage ->
            currentLanguage = newLanguage
        }
        
        LocalizationManager.addLanguageChangeListener(listener)
        
        onDispose {
            LocalizationManager.removeLanguageChangeListener(listener)
        }
    }
    
    return LocalizationManager.getString(key)
}

/**
 * Composable hook to get the current language reactively
 */
@Composable
fun rememberCurrentLanguage(): Language {
    var currentLanguage by remember { mutableStateOf(LocalizationManager.getCurrentLanguage()) }
    
    DisposableEffect(Unit) {
        val listener: (Language) -> Unit = { newLanguage ->
            currentLanguage = newLanguage
        }
        
        LocalizationManager.addLanguageChangeListener(listener)
        
        onDispose {
            LocalizationManager.removeLanguageChangeListener(listener)
        }
    }
    
    return currentLanguage
}
