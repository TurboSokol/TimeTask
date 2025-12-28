package com.turbosokol.TimeTask

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.turbosokol.TimeTask.localization.PlatformLanguageDetector

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/


class MainActivity : ComponentActivity() {
    private var languageDetector: PlatformLanguageDetector? = null
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            println("MainActivity: POST_NOTIFICATIONS permission granted")
        } else {
            println("MainActivity: POST_NOTIFICATIONS permission denied")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
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