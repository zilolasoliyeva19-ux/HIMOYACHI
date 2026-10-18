package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.model.Language
import com.example.model.RiskLevel
import com.example.model.UrlRiskStatus
import com.example.ui.theme.CyberDangerRed
import com.example.ui.theme.CyberDangerRedBg
import com.example.ui.theme.CyberSafeGreen
import com.example.ui.theme.CyberSafeGreenBg
import com.example.ui.theme.CyberWarningOrange
import com.example.ui.theme.CyberWarningOrangeBg

@Composable
fun ThreatBadge(
    riskLevel: RiskLevel,
    lang: Language,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor) = when (riskLevel) {
        RiskLevel.THREAT_CONFIRMED -> Triple(CyberDangerRedBg, CyberDangerRed, CyberDangerRed)
        RiskLevel.SUSPICIOUS -> Triple(CyberWarningOrangeBg, CyberWarningOrange, CyberWarningOrange)
        RiskLevel.SENSITIVE_PERMISSIONS -> Triple(Color(0x1F2979FF), Color(0xFF2979FF), Color(0xFF2979FF))
        RiskLevel.SAFE -> Triple(CyberSafeGreenBg, CyberSafeGreen, CyberSafeGreen)
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = AppStrings.riskLevel(lang, riskLevel),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun UrlRiskBadge(
    status: UrlRiskStatus,
    lang: Language,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor) = when (status) {
        UrlRiskStatus.PHISHING_THREAT, UrlRiskStatus.MALICIOUS -> Triple(CyberDangerRedBg, CyberDangerRed, CyberDangerRed)
        UrlRiskStatus.SUSPICIOUS -> Triple(CyberWarningOrangeBg, CyberWarningOrange, CyberWarningOrange)
        UrlRiskStatus.SAFE -> Triple(CyberSafeGreenBg, CyberSafeGreen, CyberSafeGreen)
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = AppStrings.urlStatusTitle(lang, status),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
