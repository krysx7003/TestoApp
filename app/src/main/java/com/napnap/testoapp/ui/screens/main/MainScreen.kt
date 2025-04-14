package com.napnap.testoapp.ui.screens.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.napnap.testoapp.QuizActivity
import com.napnap.testoapp.data.constants.BASE_DIR_NAME
import com.napnap.testoapp.data.constants.CONTINUE_QUIZ_EXTRA
import com.napnap.testoapp.data.constants.DIR_NAME_EXTRA
import com.napnap.testoapp.data.constants.HALF
import com.napnap.testoapp.data.constants.QUARTER
import com.napnap.testoapp.data.constants.THREE_QUARTER
import com.napnap.testoapp.data.stores.SettingsStore
import com.napnap.testoapp.data.constants.Green
import com.napnap.testoapp.data.constants.LightGreen
import com.napnap.testoapp.data.constants.LightRed
import com.napnap.testoapp.data.constants.Red
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

//FIXME - BAD HACK użyj Card
@Composable
fun MainScreen(values: PaddingValues,
                startDialogVisible: MutableState<Boolean>,
                nameOfItem: MutableState<String>,
                getUri: ActivityResultLauncher<String>
){
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
            LoadButton(getUri)
            HistoryButton(visible)
            if(visible.value){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.primary)
                ){
                    HistoryList(startDialogVisible,nameOfItem)
                }
            }
            Log.i("HistoryList","List is hidden ${!visible.value} ")
            Footer()
        }
    }
}

@Composable
fun ContinueButton(){
    val localContext = LocalContext.current
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .size(60.dp)
            .padding(vertical = 0.dp),
        onClick = { continueQuiz(localContext) },
        shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.PlayArrow,
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

fun continueQuiz(localContext:Context){
    val settingsStore = SettingsStore()
    val name = runBlocking {
        settingsStore.read("lastQuiz", localContext).first() ?: ""
    }
    if(name.isNotEmpty()){
        Log.i("ContinueQuiz","Continuing Quiz $name")
        startQuiz(name, localContext,true)
    }else{
        Toast.makeText(localContext,"Nie można kontynuować",Toast.LENGTH_SHORT).show()
        Log.w("ContinueQuiz","Attempted to continue null quiz")
    }
}

@Composable
fun LoadButton(getUri:ActivityResultLauncher<String>){
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .size(60.dp)
            .padding(vertical = 0.dp),
        onClick = {
            getUri.launch("application/zip")
 },
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
fun HistoryList(startDialogVisible:MutableState<Boolean>,nameOfItem:MutableState<String> ){
    val localContext = LocalContext.current
    val viewModel = MainViewModel(localContext)
    val items = viewModel.quizHistory.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ){
        HistoryHeader()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(items.value) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(40.dp)
                        .clickable{
                            startDialogVisible.value = true
                            nameOfItem.value = item.name
                        }
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ){
                    Text(text = item.name,
                        modifier = Modifier.weight(1f),
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val color = when{
                        item.completion<QUARTER -> LightRed
                        item.completion<HALF -> Red
                        item.completion<THREE_QUARTER -> Green
                        item.completion>THREE_QUARTER -> LightGreen
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
}

@Composable
fun HistoryHeader(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .size(40.dp)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ){
        Text(text = "Nazwa",
            modifier = Modifier.weight(1f),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(text = "%",
            modifier = Modifier.weight(1f),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(text = "Czas",
            modifier = Modifier.weight(1f),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
    Divider(color = MaterialTheme.colorScheme.onSecondary, thickness = 1.dp)
}

fun startQuiz(dirName:String,localContext: Context,continueQuiz:Boolean){
    val settingsStore = SettingsStore()
    if(isValidDir(localContext,dirName)){
        CoroutineScope(Dispatchers.IO).launch {
            settingsStore.save("lastQuiz",dirName,localContext)
            Log.i("SaveQuiz","Last Quiz is $dirName ")
        }
        Log.i("StartQuiz","Starting Quiz $dirName")
        val activity = localContext as? Activity
        activity?.let {
            val intent = Intent(it, QuizActivity::class.java).apply {
                putExtra(DIR_NAME_EXTRA,dirName)
                putExtra(CONTINUE_QUIZ_EXTRA,continueQuiz)
            }
            it.startActivity(intent)
        }
    }else{
        Toast.makeText(localContext,"Wybrany quiz nie istnieje",Toast.LENGTH_SHORT).show()
        Log.w("StartQuiz","There is no Quiz or dir $dirName")
    }
}

fun isValidDir(context: Context,dirName: String):Boolean{
    val dir = File(context.filesDir, "$BASE_DIR_NAME/$dirName")
    Log.i("IsValid","Dir $dirName exists ${dir.exists()} and is dir ${dir.isDirectory}")
    return dir.exists() && dir.isDirectory && containsTxtFiles(dir)
}

fun containsTxtFiles(dir:File):Boolean{
    val txtFiles = dir.listFiles { _, name -> name.endsWith(".txt") } ?: return false
    Log.i("ContainsFiles","Dir ${dir.name} contains $txtFiles files")
    return txtFiles.size > 2
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
