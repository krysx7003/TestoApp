package com.napnap.testoapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.napnap.testoapp.dataStores.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(values: PaddingValues,settingsStore: SettingsStore){
    Column(
        modifier = Modifier.fillMaxSize().padding(values),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current.applicationContext
        val colorModeState = settingsStore.read("mode",context).collectAsState(initial = "Dark")
        val colorMode = colorModeState.value ?: "Dark"
        Text("Settings $colorMode")
        Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    settingsStore.save("mode","Light",context)
                }
            }
        ) {
            Text("Change mode")
        }
    }
}