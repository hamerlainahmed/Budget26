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
    onPrimary = Color(0xFF022C22),
    primaryContainer = EmeraldStableDarkBg,
    onPrimaryContainer = EmeraldStableLight,
    secondary = SkyInstitutional,
    onSecondary = Color(0xFF082F49),
    secondaryContainer = Color(0xFF075985),
    onSecondaryContainer = Color(0xFFE0F2FE),
    tertiary = AmberElevated,
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = AmberElevatedDarkBg,
    onTertiaryContainer = AmberElevatedLight,
    background = SlateBackgroundDark,
    onBackground = DarkTextPrimary,
    surface = SlateSurfaceDark,
    onSurface = DarkTextPrimary,
    surfaceVariant = SlateCardDark,
    onSurfaceVariant = DarkTextSecondary,
    outline = SlateBorderDark,
    outlineVariant = SlateBorderSubtle,
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
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF075985),
    tertiary = RoyalGoldAccent,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF78350F),
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = Color(0xFFCBD5E1),
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
