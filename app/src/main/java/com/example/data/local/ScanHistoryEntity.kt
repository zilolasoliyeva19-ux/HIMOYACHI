package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val scanType: String, // "Tezkor tekshiruv" or "To'liq tekshiruv"
    val totalAppsScanned: Int,
    val threatsCount: Int,
    val suspiciousCount: Int,
    val securityScore: Int,
    val summaryText: String
)

@Entity(tableName = "url_scan_history")
data class UrlScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val domain: String,
    val riskStatus: String,
    val riskScore: Int,
    val timestamp: Long,
    val primaryReason: String
)
