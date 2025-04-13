package com.napnap.testoapp.ui.theme

import android.app.Activity
import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    var light = settingsStore.read("light",context).collectAsState(initial = "false").value.toBoolean()
    var classic = settingsStore.read("classic",context).collectAsState(initial = "false").value.toBoolean()
    var dark = settingsStore.read("dark",context).collectAsState(initial = "false").value.toBoolean()

    val allThemesFalse = !dark && !light && !classic
    Log.i("Theme","Themes are dark = $dark,light = $light,classic = $classic")

    if(allThemesFalse){
        dark = isDarkMode
        light = !isDarkMode
        saveThemesSettings(dark,light,false,context)
    }
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

fun saveThemesSettings(dark:Boolean, light: Boolean, classic:Boolean,context: Context){
    val settingsStore = SettingsStore()
    CoroutineScope(Dispatchers.IO).launch {
        settingsStore.save("dark",dark.toString(),context)
        settingsStore.save("light",light.toString(),context)
        settingsStore.save("classic",classic.toString(),context)
    }
    Log.i("SaveThemesSettings","Saved themes dark = $dark,light = $light,classic = $classic")
}