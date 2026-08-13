package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
    darkColorScheme(
        primary = RexCrimson,
        onPrimary = Color.White,
        primaryContainer = RexDarkCrimson,
        onPrimaryContainer = RexPinkText,
        secondary = SystemReadyGreen,
        onSecondary = Color.Black,
        background = DarkBackground,
        onBackground = LightText,
        surface = DarkSurface,
        onSurface = LightText,
        surfaceVariant = DarkSurfaceCard,
        onSurfaceVariant = MutedText,
        outline = DarkCardBorder
    )

@Composable
fun RexModeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

