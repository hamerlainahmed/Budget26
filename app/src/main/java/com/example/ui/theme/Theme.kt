package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldStable,
    onPrimary = Color(0xFF042F2E),
    primaryContainer = EmeraldStableDarkBg,
    onPrimaryContainer = EmeraldStableLight,
    secondary = AmberElevated,
    onSecondary = Color(0xFF3B1D06),
    secondaryContainer = AmberElevatedDarkBg,
    onSecondaryContainer = AmberElevatedLight,
    tertiary = TealSecondary,
    background = NavyPrimaryDark,
    onBackground = DarkTextPrimary,
    surface = NavySurfaceDark,
    onSurface = DarkTextPrimary,
    surfaceVariant = NavyCardDark,
    onSurfaceVariant = DarkTextSecondary,
    error = CrimsonCritical,
    onError = Color.White,
    errorContainer = CrimsonCriticalDarkBg,
    onErrorContainer = CrimsonCriticalLight
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF059669),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = RoyalGoldAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = TealSecondary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    error = Color(0xFFDC2626),
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D)
)

@Composable
fun SchoolBudget2026Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Enforce RTL layout direction for Algerian Arabic Public Accounting interface
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
