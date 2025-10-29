import SwiftUI

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

@main
struct iOSApp: App {
    
    init() {
        // Initialize notification helper
        _ = NotificationHelper.shared
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}