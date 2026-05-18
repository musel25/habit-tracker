package com.musel.habittracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = AccentSage,
    onPrimary = Color.White,
    primaryContainer = PaperSoft,
    onPrimaryContainer = Ink,
    secondary = InkSoft,
    onSecondary = Paper,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = PaperSoft,
    onSurfaceVariant = InkSoft,
    surfaceContainer = PaperSoft,
    surfaceContainerHigh = PaperSoft,
    surfaceContainerHighest = PaperLine,
    outline = PaperLine,
    outlineVariant = PaperLine,
)

private val DarkColors = darkColorScheme(
    primary = AccentSageDark,
    onPrimary = NightPaper,
    primaryContainer = NightPaperSoft,
    onPrimaryContainer = NightInk,
    secondary = NightInkSoft,
    onSecondary = NightPaper,
    background = NightPaper,
    onBackground = NightInk,
    surface = NightPaper,
    onSurface = NightInk,
    surfaceVariant = NightPaperSoft,
    onSurfaceVariant = NightInkSoft,
    surfaceContainer = NightPaperSoft,
    surfaceContainerHigh = NightPaperSoft,
    surfaceContainerHighest = NightPaperLine,
    outline = NightPaperLine,
    outlineVariant = NightPaperLine,
)

@Composable
fun HabitTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = colorScheme.background.luminance() > 0.5f
            controller.isAppearanceLightNavigationBars = colorScheme.background.luminance() > 0.5f
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
