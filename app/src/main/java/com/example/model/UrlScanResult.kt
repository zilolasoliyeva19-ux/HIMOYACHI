package com.example.model

enum class UrlRiskStatus {
    SAFE,
    SUSPICIOUS,
    PHISHING_THREAT,
    MALICIOUS
}

data class UrlScanResult(
    val url: String,
    val domain: String,
    val status: UrlRiskStatus,
    val riskScore: Int, // 0 = safe, 100 = critical
    val isHttps: Boolean,
    val isIpAddress: Boolean,
    val isMisleadingOrTyposquatting: Boolean,
    val targetedBrand: String? = null,
    val hasSuspiciousTld: Boolean,
    val hasPunycodeOrHomoglyphs: Boolean,
    val detectedRedFlags: List<String>,
    val safeAdvice: List<String>,
    val scanTimestamp: Long = System.currentTimeMillis()
)
