package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.GppMaybe
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.ProtectionState
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDangerRed
import com.example.ui.theme.CyberSafeGreen
import com.example.ui.theme.CyberWarningOrange

@Composable
fun CyberShield(
    state: ProtectionState,
    isScanning: Boolean = false,
    size: Dp = 140.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    val color = when {
        isScanning -> CyberCyan
        state == ProtectionState.SECURE -> CyberSafeGreen
        state == ProtectionState.THREAT_FOUND -> CyberDangerRed
        else -> CyberWarningOrange
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing radar circle
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerRadius = this.size.minDimension / 2f
            drawCircle(
                color = color.copy(alpha = pulseAlpha),
                radius = centerRadius * pulseScale,
                style = Stroke(width = 3.dp.toPx())
            )
            drawCircle(
                color = color.copy(alpha = 0.15f),
                radius = centerRadius * 0.9f
            )
            drawCircle(
                color = color.copy(alpha = 0.4f),
                radius = centerRadius * 0.9f,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Shield Icon
        val shieldIcon = when {
            isScanning -> Icons.Filled.Shield
            state == ProtectionState.SECURE -> Icons.Filled.GppGood
            state == ProtectionState.THREAT_FOUND -> Icons.Filled.GppBad
            else -> Icons.Filled.GppMaybe
        }

        Icon(
            imageVector = shieldIcon,
            contentDescription = "Security Shield",
            tint = color,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}
