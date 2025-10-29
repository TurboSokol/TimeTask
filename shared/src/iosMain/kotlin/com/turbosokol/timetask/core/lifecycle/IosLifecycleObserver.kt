/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.core.lifecycle

/**
 * iOS-specific lifecycle observer that integrates with AppLifecycleManager
 * Provides methods to be called from iOS UIApplicationDelegate or SwiftUI lifecycle
 */
class IosLifecycleObserver(
    private val appLifecycleManager: AppLifecycleManager
) {
    
    /**
     * Call this from applicationDidFinishLaunching or onAppear
     */
    fun applicationDidBecomeActive() {
        appLifecycleManager.onAppStart()
    }
    
    /**
     * Call this from applicationWillTerminate or onDisappear
     */
    fun applicationWillTerminate() {
        appLifecycleManager.onAppStop()
    }
    
    /**
     * Call this from applicationDidEnterBackground
     */
    fun applicationDidEnterBackground() {
        appLifecycleManager.onAppPause()
    }
    
    /**
     * Call this from applicationWillEnterForeground
     */
    fun applicationWillEnterForeground() {
        appLifecycleManager.onAppResume()
    }
}



