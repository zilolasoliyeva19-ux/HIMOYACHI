package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CyberColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = CyberDarkBg,
    primaryContainer = CyberCardElevated,
    onPrimaryContainer = CyberCyan,
    secondary = CyberBlue,
    onSecondary = CyberDarkBg,
    secondaryContainer = CyberCard,
    onSecondaryContainer = CyberCyanDim,
    tertiary = CyberSafeGreen,
    onTertiary = CyberDarkBg,
    background = CyberDarkBg,
    onBackground = CyberTextPrimary,
    surface = CyberSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberCard,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberCardBorder,
    error = CyberDangerRed,
    onError = CyberDarkBg
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Cybersecurity apps look best in dedicated dark mode
    dynamicColor: Boolean = false, // Keep consistent cyber branding
    content: @Composable () -> Unit
) {
    val colorScheme = CyberColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = CyberDarkBg.toArgb()
                window.navigationBarColor = CyberDarkBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
