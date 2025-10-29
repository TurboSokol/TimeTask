import Foundation
import UserNotifications
import UIKit

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn't work, I don't know who was created it.
 ***/

/**
 * iOS Notification Helper
 * Handles notification permissions and setup for the TimeTask app
 */
class NotificationHelper: NSObject, UNUserNotificationCenterDelegate {
    
    static let shared = NotificationHelper()
    
    private override init() {
        super.init()
        setupNotificationCenter()
    }
    
    /**
     * Setup notification center and request permissions
     */
    func setupNotificationCenter() {
        let center = UNUserNotificationCenter.current()
        center.delegate = self
        
        // Request notification permissions
        center.requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
            DispatchQueue.main.async {
                if granted {
                    print("NotificationHelper: Notification permission granted")
                } else {
                    print("NotificationHelper: Notification permission denied")
                }
                
                if let error = error {
                    print("NotificationHelper: Notification permission error: \(error.localizedDescription)")
                }
            }
        }
        
        // Configure notification categories
        setupNotificationCategories()
    }
    
    /**
     * Setup notification categories for task notifications
     */
    private func setupNotificationCategories() {
        let center = UNUserNotificationCenter.current()
        
        // Create task notification category optimized for lock screen
        let taskCategory = UNNotificationCategory(
            identifier: "TASK_NOTIFICATION",
            actions: [],
            intentIdentifiers: [],
            options: [.customDismissAction, .allowInCarPlay]
        )
        
        center.setNotificationCategories([taskCategory])
    }
    
    /**
     * Check if notifications are authorized
     */
    func checkNotificationAuthorization(completion: @escaping (Bool) -> Void) {
        UNUserNotificationCenter.current().getNotificationSettings { settings in
            DispatchQueue.main.async {
                let isAuthorized = settings.authorizationStatus == .authorized
                completion(isAuthorized)
            }
        }
    }
    
    // MARK: - UNUserNotificationCenterDelegate
    
    /**
     * Handle notification when app is in foreground
     */
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        // Don't show notifications when app is in foreground
        // They will only appear on lock screen and notification center
        // This ensures clean UI when app is open, but notifications show on lock screen
        completionHandler([])
    }
    
    /**
     * Handle notification tap
     */
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        // Handle notification tap - could open specific task or app
        print("NotificationHelper: Notification tapped: \(response.notification.request.identifier)")
        completionHandler()
    }
}
