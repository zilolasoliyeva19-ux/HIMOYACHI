package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.example.MainActivity
import com.example.util.SecurityNotificationHelper

class AppInstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        if (action == Intent.ACTION_PACKAGE_ADDED || action == Intent.ACTION_PACKAGE_REPLACED) {
            val packageName = intent.data?.schemeSpecificPart ?: return
            if (packageName == context.packageName) return // Ignore self-update

            val pm = context.packageManager
            val appLabel = try {
                val appInfo = pm.getApplicationInfo(packageName, 0)
                pm.getApplicationLabel(appInfo).toString()
            } catch (e: Exception) {
                packageName
            }

            // Post notification for user awareness
            SecurityNotificationHelper.showRogueAppNotification(context, appLabel, packageName)

            // Launch HIMOYACHI to display the real-time uninstall alert
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("EXTRA_NEW_PACKAGE_NAME", packageName)
            }
            context.startActivity(launchIntent)
        }
    }
}
