package com.turbosokol.TimeTask

import platform.UIKit.UIDevice

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()