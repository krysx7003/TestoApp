package com.napnap.testoapp

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.napnap.testoapp.data.stores.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(values: PaddingValues,settingsStore: SettingsStore){
    Column(
        modifier = Modifier.fillMaxSize().padding(values),
    ) {
        val context = LocalContext.current.applicationContext
        ThemePicker(context,settingsStore)
        QuizOptions(context,settingsStore)
    }
}

@Composable
fun ThemePicker(context: Context,settingsStore: SettingsStore){
    val isDarkMode = isSystemInDarkTheme()
    val dark = settingsStore.read("dark",context)
        .map { it == "true" }
        .collectAsState(initial = isDarkMode)
        .value
    val light = settingsStore.read("light",context)
        .map { it == "true" }
        .collectAsState(initial = !isDarkMode)
        .value
    val classic = settingsStore.read("classic",context)
        .map { it == "true" }
        .collectAsState(initial = false)
        .value
    Column {
        Text("Motywy",
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable{
                    saveThemes(dark = true,light = false, classic = false,context,settingsStore)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Text("Ciemny", fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
            Checkbox(checked = dark, onCheckedChange = {}, enabled = false)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable{
                    saveThemes(dark = false,light = true, classic = false,context,settingsStore)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Text("Jasny", fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
            Checkbox(checked = light, onCheckedChange = {}, enabled = false)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable{
                    saveThemes(dark = false,light = false, classic = true,context,settingsStore)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Text("Klasyczny", fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
            Checkbox(checked = classic, onCheckedChange = {}, enabled = false)
        }

    }
    Divider(color = MaterialTheme.colorScheme.onSecondary, thickness = 1.dp)
}

fun saveThemes(dark:Boolean,light: Boolean,classic:Boolean,context: Context,settingsStore: SettingsStore){
    //TODO - Przejście jest bardzo widoczne, można to poprawić?
    CoroutineScope(Dispatchers.IO).launch {
        settingsStore.save("dark",dark.toString(),context)
        settingsStore.save("light",light.toString(),context)
        settingsStore.save("classic",classic.toString(),context)
    }
    Log.i("SaveThemes","Saved themes dark = $dark,light = $light,classic = $classic")
}

@Composable
fun QuizOptions(context: Context,settingsStore: SettingsStore){
    Column{
        Text("Quiz",
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(10.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ){
            Text("Liczba dodatkowych powtórzeń przy błędnej odpowiedzi"
                , fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ){
            Text("Wstępna liczba powtórzeń",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ){
            Text("Maksymalna liczba powtórzeń",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}