package com.napnap.testoapp

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.napnap.testoapp.data.stores.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(values: PaddingValues,settingsStore: SettingsStore){
    val context = LocalContext.current.applicationContext
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn (
            modifier = Modifier.weight(1f).fillMaxWidth().padding(values).imePadding(),
        ) {
            item{
                ThemeOptions(context,settingsStore)
            }
            item{
                QuizOptions(context,settingsStore)
            }
        }
        }
}

@Composable
fun ThemeOptions(context: Context, settingsStore: SettingsStore){
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
        val darkTheme = listOf(true,false,false)
        ThemeOption(dark,darkTheme,"Ciemny",context)
        val lightTheme = listOf(false,true,false)
        ThemeOption(light,lightTheme,"Jasny",context)
        val classicTheme = listOf(false,false,true)
        ThemeOption(classic,classicTheme,"Klasyczny",context)
    }
    Divider(color = MaterialTheme.colorScheme.onSecondary, thickness = 1.dp)
}

@Composable
fun ThemeOption(theme:Boolean, themes:List<Boolean>,header: String, context: Context){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable{
                saveThemesSettings(dark = themes[0],light = themes[1], classic = themes[2],context)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ){
        Text(header, fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
        Checkbox(checked = theme, onCheckedChange = {}, enabled = false)
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

@Composable
fun QuizOptions(context: Context,settingsStore: SettingsStore){
    val repeatAmount = settingsStore.read("repeatAmount",context)
        .collectAsState(initial = "1")
        .value ?: "1"
    Log.i("ReadRepeatAmount","RepeatAmount is $repeatAmount")
    val startAmount = settingsStore.read("startAmount",context)
        .collectAsState(initial = "2")
        .value ?: "2"
    Log.i("ReadStartAmount","StartAmount is $startAmount")
    val maxAmount = settingsStore.read("maxAmount",context)
        .collectAsState(initial = "10")
        .value ?: "10"
    Log.i("ReadMaxAmount","MaxAmount is $maxAmount")
    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.onPrimary,
        backgroundColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f))
    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Column {
            Text(
                "Quiz",
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(10.dp)
            )
            QuizOption(repeatAmount,"Liczba dodatkowych powtórzeń przy błędnej odpowiedzi","repeatAmount",context)
            QuizOption(startAmount,"Wstępna liczba powtórzeń","startAmount",context)
            QuizOption(maxAmount,"Maksymalna liczba powtórzeń","maxAmount",context)
        }
    }
}

@Composable
fun QuizOption(initialValue:String, header:String,key:String,context: Context){
    val number = remember { mutableStateOf(initialValue) }
    LaunchedEffect(initialValue) {
        number.value = initialValue
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Text(
            header,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
            value = number.value,
            onValueChange = {
                number.value = it
                saveQuizSettings(number.value,key,context) },
            textStyle = TextStyle(fontSize = 20.sp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

fun saveQuizSettings(number:String, key:String, context: Context){
    val settingsStore = SettingsStore()
    if(number!=""){
        CoroutineScope(Dispatchers.IO).launch {
            settingsStore.save(key,number,context)
        }
        Log.i("SaveQuizSettings","Saved on key $key with value $number")
    }else{
        Log.w("SaveQuizSettings","Attempted to save empty string at key $key")
    }
}