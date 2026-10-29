package com.vibe.parentalcontrol

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var statusText: TextView
    private lateinit var deviceIdText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.status_text)
        deviceIdText = findViewById(R.id.device_id_text)
        deviceIdText.text = "ID: ${LockManager.getDeviceId(this)}"

        findViewById<Button>(R.id.btn_30min).setOnClickListener { startLock(30) }
        findViewById<Button>(R.id.btn_1h).setOnClickListener { startLock(60) }
        findViewById<Button>(R.id.btn_1h30).setOnClickListener { startLock(90) }
        findViewById<Button>(R.id.btn_2h).setOnClickListener { startLock(120) }
        findViewById<Button>(R.id.btn_stop).setOnClickListener { showStopDialog() }

        updateStatus()
    }

    private fun startLock(durationMinutes: Int) {
        if (!isAccessibilityEnabled()) {
            showAccessibilityDialog()
            return
        }
        LockManager.activateLock(this, durationMinutes)
        startService(Intent(this, LockService::class.java))
        Toast.makeText(this, "✅ Bloqué jusqu'à ${LockManager.getLockEndTime(this)}!", Toast.LENGTH_LONG).show()
        updateStatus()
    }

    private fun showStopDialog() {
        AlertDialog.Builder(this)
            .setTitle("Arrêter le blocage ?")
            .setMessage("Tu devras demander le code à Vibe pour confirmer.")
            .setPositiveButton("Oui") { _, _ ->
                LockManager.deactivateLock(this)
                stopService(Intent(this, LockService::class.java))
                Toast.makeText(this, "❌ Blocage arrêté", Toast.LENGTH_SHORT).show()
                updateStatus()
            }
            .setNegativeButton("Non", null)
            .show()
    }

    private fun isAccessibilityEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        return enabledServices?.contains("com.vibe.parentalcontrol/.AppBlockerService") == true
    }

    private fun showAccessibilityDialog() {
        AlertDialog.Builder(this)
            .setTitle("Service nécessaire")
            .setMessage("Active le service d'accessibilité pour bloquer les apps.")
            .setPositiveButton("OK") { _, _ ->
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
            .show()
    }

    private fun updateStatus() {
        if (LockManager.isLocked(this)) {
            statusText.text = "✅ Bloqué jusqu'à ${LockManager.getLockEndTime(this)}"
            statusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        } else {
            statusText.text = "❌ Pas bloqué"
            statusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        }
    }
}
