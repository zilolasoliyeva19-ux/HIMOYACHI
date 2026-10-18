package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Language
import com.example.model.UrlRiskStatus
import com.example.model.UrlScanResult
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
fun LinkSecurityModal(
    url: String,
    result: UrlScanResult?,
    isChecking: Boolean,
    lang: Language,
    onProceed: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .background(CyberDarkBg, RoundedCornerShape(22.dp))
                .border(
                    1.dp,
                    if (result?.status == UrlRiskStatus.PHISHING_THREAT || result?.status == UrlRiskStatus.MALICIOUS) {
                        CyberDangerRed
                    } else if (result?.status == UrlRiskStatus.SAFE) {
                        CyberSafeGreen
                    } else {
                        CyberCyan
                    },
                    RoundedCornerShape(22.dp)
                ),
            color = CyberDarkBg,
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HIMOYACHI • Web Qalqon",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = CyberTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isChecking) {
                    CircularProgressIndicator(
                        color = CyberCyan,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Havola xavfsizligi tekshirilmoqda...",
                        color = CyberTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = url,
                        color = CyberTextMuted,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else if (result != null) {
                    val isDangerous = result.status == UrlRiskStatus.PHISHING_THREAT || result.status == UrlRiskStatus.MALICIOUS
                    val isSafe = result.status == UrlRiskStatus.SAFE

                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(
                                if (isDangerous) CyberDangerRed.copy(alpha = 0.2f) else CyberSafeGreen.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isDangerous) Icons.Filled.Block else Icons.Filled.CheckCircle,
                            contentDescription = "Status",
                            tint = if (isDangerous) CyberDangerRed else CyberSafeGreen,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isDangerous) "XAVFLI SAYT TO'XTATILDI!" else "Xavfsiz havola!",
                        color = if (isDangerous) CyberDangerRed else CyberSafeGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = result.domain,
                        color = CyberTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isDangerous) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CyberCard),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Ushbu havola soxta (fishing) yoki firibgarlik sahifasi bo'lib, bank kartasi va SMS-kodlaringizni o'g'irlash uchun yaratilgan. Saytga o'tish bloklandi!",
                                    color = CyberTextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                                if (result.detectedRedFlags.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    result.detectedRedFlags.take(2).forEach { flag ->
                                        Text(
                                            text = "• $flag",
                                            color = CyberDangerRed,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Safe action: Cancel / Close
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberDangerRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = "Xavfdan qaytish (Tavsiya etiladi)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onProceed,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Baribir o'tish (Xavfli)",
                                color = CyberTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    } else {
                        // Safe link
                        Text(
                            text = "Havolada xavfli belgilar aniqlanmadi. Brauzerda ochilmoqda...",
                            color = CyberTextSecondary,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onProceed,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSafeGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.OpenInBrowser,
                                contentDescription = "Open",
                                tint = CyberDarkBg,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Darhol ochish",
                                color = CyberDarkBg,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
