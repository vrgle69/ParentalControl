package com.vibe.parentalcontrol

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class AppBlockerService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        if (!LockManager.isLocked(this)) return

        val packageName = event.packageName?.toString() ?: return
        if (!AppConfig.ALLOWED_APPS.contains(packageName)) {
            Toast.makeText(this, "❌ $packageName bloqué jusqu'à ${LockManager.getLockEndTime(this)}", Toast.LENGTH_SHORT).show()
            performGlobalAction(GLOBAL_ACTION_BACK)
            performGlobalAction(GLOBAL_ACTION_HOME)
        }
    }
    override fun onInterrupt() {}
}
