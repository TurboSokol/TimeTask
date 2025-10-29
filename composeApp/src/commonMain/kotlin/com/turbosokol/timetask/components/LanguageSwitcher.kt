/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.turbosokol.TimeTask.localization.Language
import com.turbosokol.TimeTask.localization.LocalizationManager

/**
 * Language switcher component that allows users to change the app language
 */
@Composable
fun LanguageSwitcher() {
    var showDropdown by remember { mutableStateOf(false) }
    val currentLanguage = LocalizationManager.getCurrentLanguage()
    
    IconButton(onClick = { showDropdown = true }) {
        Icon(
            imageVector = Icons.Default.Language,
            contentDescription = "Change language"
        )
    }
    
    DropdownMenu(
        expanded = showDropdown,
        onDismissRequest = { showDropdown = false }
    ) {
        Language.entries.forEach { language ->
            DropdownMenuItem(
                text = { 
                    Text(
                        text = when (language) {
                            Language.ENGLISH -> "English"
                            Language.RUSSIAN -> "Русский"
                        }
                    )
                },
                onClick = {
                    LocalizationManager.setLanguage(language)
                    showDropdown = false
                },
                enabled = language != currentLanguage
            )
        }
    }
}
