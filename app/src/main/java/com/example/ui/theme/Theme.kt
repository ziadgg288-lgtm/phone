package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GamingDarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3C1864),
    onPrimaryContainer = NeonPurpleLight,
    secondary = ElectricCyan,
    onSecondary = VoidBlack,
    secondaryContainer = Color(0xFF00384D),
    onSecondaryContainer = ElectricCyan,
    tertiary = CyberPink,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCardElevated,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
    outlineVariant = Color(0xFF1E2638)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force professional gaming dark theme by default
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GamingDarkColorScheme,
        typography = Typography,
        content = content
    )
}
