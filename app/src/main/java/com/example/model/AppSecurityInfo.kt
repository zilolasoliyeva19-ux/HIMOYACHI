package com.example.model

enum class RiskLevel {
    THREAT_CONFIRMED,       // High-risk or known malicious pattern / dropper
    SUSPICIOUS,             // Dangerous permission combo or unverified origin mimicking brand
    SENSITIVE_PERMISSIONS,  // Harmless app but holding elevated permissions (e.g. Accessibility or Overlay)
    SAFE                    // No anomalies or suspicious permission combinations
}

data class AppThreatModel(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val isSystemApp: Boolean,
    val isSideloaded: Boolean,
    val riskLevel: RiskLevel,
    val riskScore: Int, // 0 (safest) to 100 (highest risk)
    val reasons: List<String>,
    val permissions: List<String>,
    val highRiskPermissions: List<String>,
    val apkSha256: String = "",
    val apkSizeMb: Double = 0.0,
    val firstInstallTime: Long = 0L,
    val lastUpdateTime: Long = 0L
)

enum class SensitivePermissionType(val id: String, val androidPermission: String) {
    CAMERA("camera", "android.permission.CAMERA"),
    MICROPHONE("mic", "android.permission.RECORD_AUDIO"),
    LOCATION("location", "android.permission.ACCESS_FINE_LOCATION"),
    SMS("sms", "android.permission.RECEIVE_SMS"),
    CONTACTS("contacts", "android.permission.READ_CONTACTS"),
    OVERLAY("overlay", "android.permission.SYSTEM_ALERT_WINDOW"),
    ACCESSIBILITY("accessibility", "android.permission.BIND_ACCESSIBILITY_SERVICE"),
    INSTALL_PACKAGES("install_unknown", "android.permission.REQUEST_INSTALL_PACKAGES")
}

data class PermissionGroupApps(
    val type: SensitivePermissionType,
    val apps: List<AppThreatModel>
)
