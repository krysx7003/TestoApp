package com.napnap.testoapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.napnap.testoapp.data.classes.Quiz
import com.napnap.testoapp.data.stores.SettingsStore
import com.napnap.testoapp.ui.theme.Green
import com.napnap.testoapp.ui.theme.LightGreen
import com.napnap.testoapp.ui.theme.LightRed
import com.napnap.testoapp.ui.theme.Red
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun MainScreen(values: PaddingValues){
    val visible = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(values),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Testownik",fontSize = 60.sp, color = MaterialTheme.colorScheme.onPrimary)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            ContinueButton()
            LoadButton()
            HistoryButton(visible)
            if(visible.value){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.primary)
                ){
                    HistoryList()
                }
            }
            Log.i("HistoryList","List is hidden ${!visible.value} ")
            Footer()
        }
    }
}

@Composable
fun ContinueButton(){
    val context = LocalContext.current.applicationContext
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .size(60.dp)
            .padding(vertical = 0.dp),
        onClick = { continueQuiz(context) },
        shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
    ) {
        Icon(imageVector = Icons.Filled.PlayArrow,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(30.dp)
        )
        Text("Kontynuuj",
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge
        )
    }
    Divider(color = MaterialTheme.colorScheme.onSecondary, thickness = 2.dp)
}

fun continueQuiz(context: Context){
    val settingsStore = SettingsStore()
    val name = runBlocking {
        settingsStore.read("lastQuiz", context).first() ?: ""
    }
    if(name.isNotEmpty()){
        Log.i("ContinueQuiz","Continuing Quiz $name")
        startQuiz(name,context)
    }else{
        Toast.makeText(context,"Nie można kontynuować",Toast.LENGTH_SHORT).show()
        Log.w("ContinueQuiz","Attempted to continue null quiz")
    }
}

@Composable
fun LoadButton(){
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .size(60.dp)
            .padding(vertical = 0.dp),
        onClick = { loadQuiz()},
        shape = RoundedCornerShape(0.dp)
    ) {
        Text("Wczytaj",
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge
        )
    }
    Divider(color = MaterialTheme.colorScheme.onSecondary, thickness = 2.dp)
}

fun loadQuiz() {
    Log.i("LoadQuiz","Loading Quiz ")
    /*
    * TODO - Wyświetla okno dialogowe z wyborem
    *  Ładowanie z pliku .zip lub z githuba
    *  Po wybraniu opcji rozpakowuje odpowiedni plik
    *  Zawartość zostaje zapisana w nowym folderze (Nazwa??)
    *  Jeśli nazwy się powtarzają usuwamy stary folder i tworzymy nowy
    *  Nowy quiz zostaje automatycznie odpalony
    * */

}

@Composable
fun HistoryButton(visible : MutableState<Boolean>){
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .size(60.dp)
            .padding(vertical = 0.dp),
        onClick = {
            visible.value = !visible.value
        },
        shape = RoundedCornerShape(0.dp)
    ){
        val icon = if(visible.value){
            Icons.Filled.KeyboardArrowUp
        }else{
            Icons.Filled.KeyboardArrowDown
        }
        Text("Historia",
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,)
        Icon(imageVector = icon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(30.dp)
        )
    }
    Divider(color = MaterialTheme.colorScheme.onSecondary, thickness = 2.dp)
}

@Composable
fun HistoryList(){
    val context = LocalContext.current.applicationContext
    //TODO - To powininno być w viewModelu
    val itemList = listOf(
        Quiz("Task 1", 23.5, "12:30:45"),
        Quiz("Task 2", 72.3, "08:15:20"),
        Quiz("Task 3", 90.0, "14:05:10"),
        Quiz("Task 4", 49.0, "10:40:05"),
        Quiz("Task 5", 95.6, "09:30:55")
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp),
    ) {
        items(itemList) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(40.dp)
                    .clickable{startQuiz(item.name,context)}
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ){
                Text(text = item.name,
                    modifier = Modifier.weight(1f),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                )
                //TODO - Kolor powinien być zależny od stopnia wykonania
                val color = when{
                    item.completion<25.0 -> Red
                    item.completion<50.0 -> LightRed
                    item.completion<75.0 -> LightGreen
                    item.completion>75.0 -> Green
                    else -> MaterialTheme.colorScheme.onPrimary
                }
                Text(text = "${item.completion}%",
                    modifier = Modifier.weight(1f),
                    fontSize = 20.sp,
                    color = color,
                    style = MaterialTheme.typography.bodyLarge,)
                Text(text = item.time,
                    modifier = Modifier.weight(1f),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge,)
            }
            Divider(color = MaterialTheme.colorScheme.onSecondary, thickness = 1.dp)
        }
    }
}

fun startQuiz(dirName:String,context: Context){
    val settingsStore = SettingsStore()
    //TODO - Sprawdź czy taki folder istnieje
    CoroutineScope(Dispatchers.IO).launch {
        settingsStore.save("lastQuiz",dirName,context)
        Log.i("SaveQuiz","Last Quiz is $dirName ")
    }
    Log.i("StartQuiz","Starting Quiz $dirName")
    //TODO - Przejdź do QuestionActivity
}

@Composable
fun Footer() {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .size(15.dp)
        .padding(vertical = 0.dp),
        shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp),
        color = MaterialTheme.colorScheme.primary) {  }
}