package com.turbosokol.TimeTask

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.turbosokol.TimeTask.localization.PlatformLanguageDetector

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/


class MainActivity : ComponentActivity() {
    private var languageDetector: PlatformLanguageDetector? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            languageDetector = remember { PlatformLanguageDetector() }
            languageDetector?.setContext(context)
            
            MainScreenWithBottomNavBar()
        }
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        languageDetector?.onConfigurationChanged(newConfig)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        languageDetector?.cleanup()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    MainScreenWithBottomNavBar()
}