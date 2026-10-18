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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.model.DeviceSecurityState
import com.example.model.Language
import com.example.model.ProtectionState
import com.example.ui.components.CyberShield
import com.example.ui.theme.CyberCard
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDangerRed
import com.example.ui.theme.CyberDangerRedBg
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSafeGreen
import com.example.ui.theme.CyberSafeGreenBg
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberWarningOrange
import com.example.ui.theme.CyberWarningOrangeBg
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    deviceState: DeviceSecurityState,
    lang: Language,
    isWebGuardEnabled: Boolean,
    isAppInstallGuardEnabled: Boolean,
    onToggleWebGuard: () -> Unit,
    onToggleAppInstallGuard: () -> Unit,
    onStartScan: () -> Unit,
    onPurgeThreats: () -> Unit,
    onViewScanner: () -> Unit,
    onOpenPermissionsOnboarding: () -> Unit,
    onOpenAccessibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val lastScanText = if (deviceState.lastScanTimestamp > 0L) {
        dateFormat.format(Date(deviceState.lastScanTimestamp))
    } else {
        AppStrings.neverScanned(lang)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Central Security Shield Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CyberShield(
                        state = deviceState.protectionState,
                        size = 130.dp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val (badgeBg, badgeText, badgeBorder) = when (deviceState.protectionState) {
                        ProtectionState.SECURE -> Triple(CyberSafeGreenBg, CyberSafeGreen, CyberSafeGreen)
                        ProtectionState.THREAT_FOUND -> Triple(CyberDangerRedBg, CyberDangerRed, CyberDangerRed)
                        ProtectionState.SCAN_NEEDED -> Triple(CyberWarningOrangeBg, CyberWarningOrange, CyberWarningOrange)
                    }

                    Box(
                        modifier = Modifier
                            .background(badgeBg, RoundedCornerShape(12.dp))
                            .border(1.dp, badgeBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = AppStrings.protectionState(lang, deviceState.protectionState),
                            color = badgeText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Xavfsizlik darajasi: ${deviceState.overallSecurityScore}/100 • ${deviceState.totalAppsScanned} ta ilova",
                        color = CyberTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Scan button
                    Button(
                        onClick = onStartScan,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("scan_device_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Scan",
                            tint = CyberDarkBg,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.scanDeviceButton(lang),
                            color = CyberDarkBg,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }



        // Threat Purge Card (If any threat found)
        if (deviceState.threatCount > 0 || deviceState.suspiciousCount > 0) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberDangerRedBg),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberDangerRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "Alert",
                                tint = CyberDangerRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${deviceState.threatCount + deviceState.suspiciousCount} ta xavfli ilova topildi!",
                                color = CyberDangerRed,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Ushbu ilovalar qurilmangizdagi bank ma'lumotlari yoki shaxsiy xabarlarni xavf ostiga qo'yadi. Ularni hoziroq o'chirib yuboring.",
                            color = CyberTextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onPurgeThreats,
                                colors = ButtonDefaults.buttonColors(containerColor = CyberDangerRed),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("purge_threats_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Purge",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Xavfli apklarni o'chirish",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = onViewScanner,
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCardElevated),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(42.dp)
                            ) {
                                Text(
                                    text = "Ro'yxat",
                                    color = CyberCyan,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Real-Time Protection Guards Header
        item {
            Text(
                text = "Real vaqt himoyasi",
                color = CyberTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Web Link Interceptor Guard
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(CyberCyan.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Link,
                            contentDescription = "Link Guard",
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Havolalarni himoyalash",
                            color = CyberTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Boshqa ilovalarda havola bosilganda xavfsiz bo'lsa o'tadi, xavfli bo'lsa pop-up to'xtatadi",
                            color = CyberTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Switch(
                        checked = isWebGuardEnabled,
                        onCheckedChange = { onToggleWebGuard() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberDarkBg,
                            checkedTrackColor = CyberCyan,
                            uncheckedThumbColor = CyberTextMuted,
                            uncheckedTrackColor = CyberCardElevated
                        )
                    )
                }
            }
        }

        // Default Browser Guard Setup Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(CyberSafeGreen.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = "Default Browser",
                                tint = CyberSafeGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Standart Veb Himoyachi",
                                color = CyberTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Barcha ilovalardagi havolalarni ochishdan oldin tekshirish va fishingni bloklash",
                                color = CyberTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onOpenAccessibility, // opens default apps settings
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Text(
                                text = "Standart qilish",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onOpenPermissionsOnboarding,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCardElevated),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Text(
                                text = "Ruxsatlar",
                                color = CyberTextPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // APK Install Guard
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(CyberSafeGreen.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = "APK Guard",
                            tint = CyberSafeGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "APK o'rnatish nazorati",
                            color = CyberTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Xavfli APK o'rnatilayotganda to'xtatib qoladi va o'chirish pop-upini chiqaradi",
                            color = CyberTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Switch(
                        checked = isAppInstallGuardEnabled,
                        onCheckedChange = { onToggleAppInstallGuard() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberDarkBg,
                            checkedTrackColor = CyberSafeGreen,
                            uncheckedThumbColor = CyberTextMuted,
                            uncheckedTrackColor = CyberCardElevated
                        )
                    )
                }
            }
        }

        // Footer
        item {
            Text(
                text = "${AppStrings.lastScan(lang)}: $lastScanText",
                color = CyberTextMuted,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
