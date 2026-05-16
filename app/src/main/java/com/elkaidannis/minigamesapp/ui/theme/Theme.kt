package com.elkaidannis.minigamesapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColorScheme = darkColorScheme(
    primary          = PurpleAccent,
    onPrimary        = Color.White,
    background       = DarkBackground,
    onBackground     = PrimaryText,
    surface          = DarkSurface,
    onSurface        = PrimaryText,
    surfaceVariant   = DarkSurfaceVariant,
    onSurfaceVariant = SecondaryText,
    outline          = DarkBorder,
)

@Composable
fun MiniGamesAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}
