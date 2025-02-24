package com.napnap.testoapp.ui.theme

import android.app.Activity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.napnap.testoapp.data.stores.SettingsStore
import kotlinx.coroutines.flow.map

private val DarkColorScheme = darkColorScheme(
    primary = BlueGrayMain,
    secondary = BlueGrayBackground,
    background = BlueGrayBackground,
    onPrimary = White,
    onSecondary = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = LightGrey,
    secondary = White,
    background = White,
    onPrimary = Color.Black,
    onSecondary = Color.Black
)

private val ClassicColorScheme = lightColorScheme(
    primary = BlueMain,
    secondary = BlueBackground,
    background = BlueBackground,
    onPrimary = Color.White,
    onSecondary = Color.White
)
@Composable
fun TestoAppTheme(
    settingsStore: SettingsStore,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isDarkMode = isSystemInDarkTheme()

    val light = settingsStore.read("light",context)
        .map { it == "true" }
        .collectAsState(initial = !isDarkMode)
        .value
    val classic = settingsStore.read("classic",context)
        .map { it == "true" }
        .collectAsState(initial = false)
        .value

    val colorScheme = when {
        light -> LightColorScheme
        classic -> ClassicColorScheme
        else -> DarkColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = light
        }
    }
    Crossfade(targetState = colorScheme) { scheme ->
        MaterialTheme(
            colorScheme = scheme,
            typography = Typography,
            content = content
        )
    }
}