package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.model.AppThreatModel
import com.example.model.Language
import com.example.model.RiskLevel
import com.example.ui.ScanFilter
import com.example.ui.components.ThreatBadge
import com.example.ui.theme.CyberCard
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDangerRed
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSafeGreen
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberWarningOrange

@Composable
fun ScannerScreen(
    isScanning: Boolean,
    scanProgress: Float,
    currentScannedAppName: String,
    apps: List<AppThreatModel>,
    selectedFilter: ScanFilter,
    lang: Language,
    onStartScan: () -> Unit,
    onCancelScan: () -> Unit,
    onFilterSelect: (ScanFilter) -> Unit,
    onAppClick: (AppThreatModel) -> Unit,
    onUninstallApp: (AppThreatModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredApps = when (selectedFilter) {
        ScanFilter.ALL -> apps
        ScanFilter.THREATS -> apps.filter { it.riskLevel == RiskLevel.THREAT_CONFIRMED }
        ScanFilter.SUSPICIOUS -> apps.filter { it.riskLevel == RiskLevel.SUSPICIOUS }
        ScanFilter.SAFE -> apps.filter { it.riskLevel == RiskLevel.SAFE || it.riskLevel == RiskLevel.SENSITIVE_PERMISSIONS }
    }

    val threatCount = apps.count { it.riskLevel == RiskLevel.THREAT_CONFIRMED }
    val suspiciousCount = apps.count { it.riskLevel == RiskLevel.SUSPICIOUS }
    val safeCount = apps.count { it.riskLevel == RiskLevel.SAFE || it.riskLevel == RiskLevel.SENSITIVE_PERMISSIONS }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 16.dp)
    ) {
        // Active scan progress
        if (isScanning) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberCardElevated),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ilovalar tekshirilmoqda...",
                            color = CyberCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${(scanProgress * 100).toInt()}%",
                            color = CyberTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { scanProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = CyberCyan,
                        trackColor = CyberCardBorder
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = currentScannedAppName,
                        color = CyberTextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onCancelScan,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberDangerRed),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Cancel", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(AppStrings.cancelScan(lang), fontSize = 11.sp)
                    }
                }
            }
        } else {
            // Scanner Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ilovalar xavfsizligi",
                        color = CyberTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${apps.size} ta ilova tekshirilgan",
                        color = CyberTextMuted,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onStartScan,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("rescan_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Rescan",
                        tint = CyberDarkBg,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppStrings.rescan(lang),
                        color = CyberDarkBg,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Clean Minimal Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedFilter == ScanFilter.ALL,
                    onClick = { onFilterSelect(ScanFilter.ALL) },
                    label = { Text("Hammasi (${apps.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan,
                        selectedLabelColor = CyberDarkBg,
                        containerColor = CyberCard,
                        labelColor = CyberTextSecondary
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == ScanFilter.THREATS,
                    onClick = { onFilterSelect(ScanFilter.THREATS) },
                    label = { Text("Xavfli ($threatCount)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberDangerRed,
                        selectedLabelColor = Color.White,
                        containerColor = CyberCard,
                        labelColor = if (threatCount > 0) CyberDangerRed else CyberTextSecondary
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == ScanFilter.SUSPICIOUS,
                    onClick = { onFilterSelect(ScanFilter.SUSPICIOUS) },
                    label = { Text("Shubhali ($suspiciousCount)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberWarningOrange,
                        selectedLabelColor = CyberDarkBg,
                        containerColor = CyberCard,
                        labelColor = if (suspiciousCount > 0) CyberWarningOrange else CyberTextSecondary
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == ScanFilter.SAFE,
                    onClick = { onFilterSelect(ScanFilter.SAFE) },
                    label = { Text("Toza ($safeCount)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberSafeGreen,
                        selectedLabelColor = CyberDarkBg,
                        containerColor = CyberCard,
                        labelColor = CyberTextSecondary
                    )
                )
            }
        }

        // List
        if (filteredApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Empty",
                        tint = CyberSafeGreen,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Ushbu toifada ilovalar mavjud emas",
                        color = CyberTextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredApps, key = { it.packageName }) { app ->
                    val isDangerous = app.riskLevel == RiskLevel.THREAT_CONFIRMED || app.riskLevel == RiskLevel.SUSPICIOUS
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberCard),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (app.riskLevel == RiskLevel.THREAT_CONFIRMED) CyberDangerRed.copy(alpha = 0.6f)
                            else if (app.riskLevel == RiskLevel.SUSPICIOUS) CyberWarningOrange.copy(alpha = 0.6f)
                            else CyberCardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAppClick(app) }
                            .testTag("app_card_${app.packageName}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        if (app.riskLevel == RiskLevel.THREAT_CONFIRMED) CyberDangerRed.copy(alpha = 0.2f)
                                        else if (app.riskLevel == RiskLevel.SUSPICIOUS) CyberWarningOrange.copy(alpha = 0.2f)
                                        else CyberCyan.copy(alpha = 0.15f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Android,
                                    contentDescription = app.appName,
                                    tint = if (app.riskLevel == RiskLevel.THREAT_CONFIRMED) CyberDangerRed
                                    else if (app.riskLevel == RiskLevel.SUSPICIOUS) CyberWarningOrange
                                    else CyberCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.appName,
                                    color = CyberTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = app.packageName,
                                    color = CyberTextMuted,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (app.reasons.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = app.reasons.first(),
                                        color = if (app.riskLevel == RiskLevel.THREAT_CONFIRMED) CyberDangerRed else CyberWarningOrange,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            if (isDangerous && !app.isSystemApp) {
                                Button(
                                    onClick = { onUninstallApp(app) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberDangerRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Uninstall",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "O'chirish",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                ThreatBadge(riskLevel = app.riskLevel, lang = lang)
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
