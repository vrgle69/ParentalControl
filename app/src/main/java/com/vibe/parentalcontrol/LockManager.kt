package com.vibe.parentalcontrol

import android.content.Context
import android.content.SharedPreferences
import java.util.*

object LockManager {
    private const val PREFS_NAME = "ParentalControlPrefs"
    private const val KEY_LOCK_END_TIME = "lock_end_time"
    private const val KEY_LOCK_CODE = "lock_code"
    private const val KEY_DEVICE_ID = "device_id"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getDeviceId(context: Context): String {
        val prefs = getPrefs(context)
        var deviceId = prefs.getString(KEY_DEVICE_ID, null)
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
        }
        return deviceId
    }

    fun activateLock(context: Context, durationMinutes: Int): String {
        val endTime = Calendar.getInstance().apply {
            add(Calendar.MINUTE, durationMinutes)
        }.timeInMillis
        val lockCode = UUID.randomUUID().toString().substring(0, 8)
        getPrefs(context).edit()
            .putLong(KEY_LOCK_END_TIME, endTime)
            .putString(KEY_LOCK_CODE, lockCode)
            .apply()
        return lockCode
    }

    fun deactivateLock(context: Context) {
        getPrefs(context).edit()
            .remove(KEY_LOCK_END_TIME)
            .remove(KEY_LOCK_CODE)
            .apply()
    }

    fun isLocked(context: Context): Boolean {
        val prefs = getPrefs(context)
        val endTime = prefs.getLong(KEY_LOCK_END_TIME, 0)
        if (endTime == 0L) return false
        return System.currentTimeMillis() < endTime
    }

    fun getLockEndTime(context: Context): String {
        val prefs = getPrefs(context)
        val endTime = prefs.getLong(KEY_LOCK_END_TIME, 0)
        if (endTime == 0L) return "N/A"
        val calendar = Calendar.getInstance().apply { timeInMillis = endTime }
        return "${String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))}:${String.format("%02d", calendar.get(Calendar.MINUTE))}"
    }

    fun getLockCode(context: Context): String? {
        return getPrefs(context).getString(KEY_LOCK_CODE, null)
    }
}
