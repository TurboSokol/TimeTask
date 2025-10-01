/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

package com.turbosokol.TimeTask.core.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Android-specific lifecycle observer that integrates with AppLifecycleManager
 * Observes Android component lifecycle and translates to app lifecycle events
 */
class AndroidLifecycleObserver(
    private val appLifecycleManager: AppLifecycleManager
) : DefaultLifecycleObserver {
    
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        appLifecycleManager.onAppStart()
    }
    
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        appLifecycleManager.onAppStop()
    }
    
    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        appLifecycleManager.onAppPause()
    }
    
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        appLifecycleManager.onAppResume()
    }
}



