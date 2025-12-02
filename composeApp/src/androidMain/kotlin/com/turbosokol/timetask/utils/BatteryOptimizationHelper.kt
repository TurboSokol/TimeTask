/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

/**
 * Helper for managing battery optimization settings.
 * Required for stable 6+ hour foreground service operation.
 */
object BatteryOptimizationHelper {
    
    /**
     * Check if battery optimization is disabled for the app
     */
    fun isBatteryOptimizationDisabled(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            return powerManager.isIgnoringBatteryOptimizations(context.packageName)
        }
        return true // Not applicable for older versions
    }
    
    /**
     * Create intent to request battery optimization exemption
     * User will be directed to system settings
     */
    fun createBatteryOptimizationIntent(context: Context): Intent {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:${context.packageName}")
        }
        return intent
    }
    
    /**
     * Create intent to open battery optimization settings page
     */
    fun createBatteryOptimizationSettingsIntent(): Intent {
        return Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
    }
    
    /**
     * Get user-friendly message explaining why battery optimization should be disabled
     */
    fun getBatteryOptimizationMessage(): String {
        return """
            For stable timer operation, please disable battery optimization for this app.
            
            Why this is needed:
            • Ensures timer continues running for 6+ hours
            • Prevents system from stopping the foreground service
            • Guarantees accurate time tracking
            
            The app will show a notification while timer is active.
        """.trimIndent()
    }
    
    /**
     * Get manufacturer-specific battery optimization instructions
     */
    fun getManufacturerSpecificInstructions(): String {
        val manufacturer = Build.MANUFACTURER.lowercase()
        
        return when {
            manufacturer.contains("samsung") -> """
                Samsung devices:
                1. Go to Settings > Apps
                2. Select TimeTask
                3. Tap Battery
                4. Select "Unrestricted"
            """.trimIndent()
            
            manufacturer.contains("huawei") || manufacturer.contains("honor") -> """
                Huawei/Honor devices:
                1. Go to Settings > Battery
                2. Disable Power saving mode
                3. Go to Apps > TimeTask
                4. Enable "Run in background"
            """.trimIndent()
            
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || manufacturer.contains("poco") -> """
                Xiaomi/Redmi/POCO devices:
                1. Go to Settings > Apps > Manage apps
                2. Select TimeTask
                3. Enable "Autostart"
                4. Tap "Battery saver" > Select "No restrictions"
            """.trimIndent()
            
            manufacturer.contains("oppo") || manufacturer.contains("realme") -> """
                OPPO/Realme devices:
                1. Go to Settings > Battery
                2. Tap "Power saving mode" and disable
                3. Go to Apps > TimeTask
                4. Enable "Run in background"
            """.trimIndent()
            
            manufacturer.contains("vivo") -> """
                Vivo devices:
                1. Go to Settings > Battery
                2. Tap "Background power consumption"
                3. Select TimeTask
                4. Enable "Allow background activity"
            """.trimIndent()
            
            else -> """
                For your device:
                1. Open Settings
                2. Search for "Battery optimization"
                3. Find TimeTask and select "Don't optimize"
                
                For more details, visit: https://dontkillmyapp.com/
            """.trimIndent()
        }
    }
}

