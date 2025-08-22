package com.threekidsinatrenchcoat.giftideaminder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

private val DarkColorScheme = darkColorScheme(
    primary = WarmCoral80,
    onPrimary = Color(0xFF2D1B0E),
    primaryContainer = Color(0xFF3D2C1A), 
    onPrimaryContainer = WarmCoral80,
    secondary = RoseGold80,
    onSecondary = Color(0xFF1D1B1B),
    secondaryContainer = Color(0xFF2E2A2A),
    onSecondaryContainer = RoseGold80,
    tertiary = SoftPeach80,
    onTertiary = Color(0xFF1A1612),
    tertiaryContainer = Color(0xFF2A251E),
    onTertiaryContainer = SoftPeach80,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF2D0A0A),
    errorContainer = Color(0xFF3D1414),
    onErrorContainer = Color(0xFFFFB4AB),
    background = Color(0xFF1A1A1A),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1A1A1A),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFC9C5C9),
    outline = Color(0xFF938F94),
    outlineVariant = Color(0xFF49454F)
)

private val LightColorScheme = lightColorScheme(
    primary = WarmCoral40,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFF0F3),
    onPrimaryContainer = Color(0xFF3D0A1A),
    secondary = RoseGold40,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFF0F0),
    onSecondaryContainer = Color(0xFF2D1414),
    tertiary = SoftPeach40,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFF3E6),
    onTertiaryContainer = Color(0xFF2D1A0A),
    error = ErrorCoral,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFEDED),
    onErrorContainer = Color(0xFF2D0A0A),
    background = Cream,
    onBackground = Forest,
    surface = Color(0xFFFFFFFF),
    onSurface = Forest,
    surfaceVariant = Color(0xFFF7F2F5),
    onSurfaceVariant = ForestLight,
    outline = WarmGray,
    outlineVariant = Color(0xFFE0D5DA),
    surfaceContainer = Color(0xFFFDF9FA),
    surfaceContainerHigh = Color(0xFFF8F4F5),
    surfaceContainerHighest = Color(0xFFF2EEEF)
)
@Preview
@Composable
fun GiftIdeaMinderTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 