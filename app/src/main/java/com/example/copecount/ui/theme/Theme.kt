package com.example.copecount.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Lilac,
    secondary = SurfaceOLEDVariant,
    tertiary = AccentPink,
    background = BackgroundOLED,
    surface = SurfaceOLED,
    onPrimary = BackgroundOLED,
    onSecondary = Cream,
    onTertiary = Cream,
    onBackground = Cream,
    onSurface = Cream,
    surfaceVariant = GlassWhite,
    outline = GlassBorder
)

// Minimal light mode support, optimized for readability
private val LightColorScheme = lightColorScheme(
    primary = Lilac,
    secondary = DoveGrey,
    tertiary = AccentPink,
    background = Color(0xFFF1F5F9),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = BackgroundOLED,
    onTertiary = BackgroundOLED,
    onBackground = BackgroundOLED,
    onSurface = BackgroundOLED
)

@Composable
fun CopeCountTheme(
    darkTheme: Boolean = true, // Default to Dark/OLED
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
