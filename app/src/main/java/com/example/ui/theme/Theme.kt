package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CosmicColorScheme = darkColorScheme(
    primary = NebulaViolet,
    secondary = StardustTeal,
    tertiary = AstralGold,
    background = CosmicBackground,
    surface = CosmicSurface,
    surfaceVariant = CosmicSurfaceVariant,
    onPrimary = OnCosmicBackground,
    onSecondary = CosmicBackground,
    onTertiary = CosmicBackground,
    onBackground = OnCosmicBackground,
    onSurface = OnCosmicSurface,
    onSurfaceVariant = OnCosmicSurfaceVariant
)

// Fallback light scheme (for high contrast if dynamic or light is selected)
private val SanctuaryLightColorScheme = lightColorScheme(
    primary = NebulaViolet,
    secondary = StardustTeal,
    tertiary = AstralGold,
    background = OnCosmicBackground,
    surface = OnCosmicSurface,
    onPrimary = OnCosmicBackground,
    onSecondary = CosmicBackground,
    onBackground = CosmicBackground,
    onSurface = CosmicBackground
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // We force cosmic theme to retain the premium sanctuary look
    dynamicColor: Boolean = false, // Disable to preserve our signature visual design identity
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CosmicColorScheme else SanctuaryLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
