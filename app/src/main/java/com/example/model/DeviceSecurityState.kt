package com.example.model

enum class ProtectionState {
    SECURE,         // Himoyalangan
    THREAT_FOUND,   // Xavf aniqlandi
    SCAN_NEEDED     // Tekshiruv kerak
}

data class DeviceSecurityState(
    val isScreenLockSet: Boolean = true,
    val isDeveloperOptionsEnabled: Boolean = false,
    val isAdbDebuggingEnabled: Boolean = false,
    val isInstallUnknownAppsRestricted: Boolean = true,
    val androidVersion: String = "",
    val securityPatchDate: String = "",
    val wifiSsid: String? = null,
    val wifiSecurityType: String = "WPA2/WPA3",
    val isWifiOpen: Boolean = false,
    val isVpnConnected: Boolean = false,
    val deviceHardeningScore: Int = 85,
    val overallSecurityScore: Int = 92,
    val protectionState: ProtectionState = ProtectionState.SECURE,
    val lastScanTimestamp: Long = 0L,
    val totalAppsScanned: Int = 0,
    val threatCount: Int = 0,
    val suspiciousCount: Int = 0,
    val sensitivePermissionCount: Int = 0
)

data class SecurityRecommendation(
    val id: String,
    val titleUz: String,
    val titleRu: String,
    val titleEn: String,
    val descriptionUz: String,
    val descriptionRu: String,
    val descriptionEn: String,
    val severity: RiskLevel,
    val isResolved: Boolean = false,
    val actionType: String // e.g. "SECURITY_SETTINGS", "PERMISSIONS", "WIFI", "PASSWORD"
)

data class EmergencyGuideItem(
    val id: String,
    val iconName: String,
    val titleUz: String,
    val titleRu: String,
    val titleEn: String,
    val summaryUz: String,
    val summaryRu: String,
    val summaryEn: String,
    val stepsUz: List<String>,
    val stepsRu: List<String>,
    val stepsEn: List<String>,
    val urgentActionPhone: String? = null
)
