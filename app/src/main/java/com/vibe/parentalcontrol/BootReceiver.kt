package com.vibe.parentalcontrol

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (LockManager.isLocked(context)) {
            context.startService(Intent(context, LockService::class.java))
        }
    }
}
