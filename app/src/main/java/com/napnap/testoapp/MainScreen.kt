package com.napnap.testoapp

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.napnap.testoapp.data.classes.Quiz

@Composable
fun MainScreen(values: PaddingValues){
    //TODO - To powininno być w viewModelu
    val itemList = listOf(
        Quiz("Task 1", 85.5, "12:30:45"),
        Quiz("Task 2", 72.3, "08:15:20"),
        Quiz("Task 3", 90.0, "14:05:10"),
        Quiz("Task 4", 50.0, "10:40:05"),
        Quiz("Task 5", 95.6, "09:30:55")
    )
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
            //TODO - To wszystko powinno wylecieć do osobnych funkcji
            //TODO - Ikony
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(60.dp)
                    .padding(vertical = 0.dp),
                onClick = { continueQuiz() },
                shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
            ) { Text("Kontynuuj",
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge,) }
            Divider(color = Color.Black, thickness = 2.dp)
            //TODO - Ikony
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(60.dp)
                    .padding(vertical = 0.dp),
                onClick = { loadQuiz()},
                shape = RoundedCornerShape(0.dp)
            ) { Text("Wczytaj",
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge,)}
            Divider(color = Color.Black, thickness = 2.dp)
            //TODO - Ikony
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(60.dp)
                    .padding(vertical = 0.dp),
                onClick = {
                    visible.value = !visible.value
                    hideQuizList(visible.value)},
                shape = RoundedCornerShape(0.dp)
            ){ Text("Historia",
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge,)
            }
            Divider(color = Color.Black, thickness = 2.dp)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 0.dp)
                    .background(MaterialTheme.colorScheme.primary),
            ) {
                items(itemList) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(40.dp)
                            .clickable{}
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
                        Text(text = "${item.completion}%",
                            modifier = Modifier.weight(1f),
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyLarge,)
                        Text(text = item.time,
                            modifier = Modifier.weight(1f),
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyLarge,)
                    }
                    Divider(color = Color.Black, thickness = 1.dp)
                }
            }
            Surface(modifier = Modifier
                .fillMaxWidth()
                .size(15.dp)
                .padding(vertical = 0.dp),
                shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp),
                color = MaterialTheme.colorScheme.primary) {  }
        }
    }
}

fun loadQuiz() {
    Log.i("LoadQuiz","Loading Quiz ")
    //TODO - Dodać funkcjonalność
}

fun continueQuiz(){
    Log.i("ContinueQuiz","Continuing Quiz ")
    //TODO - Dodać funkcjonalność
}

fun hideQuizList(visible:Boolean){
    Log.i("HideQuizList","List is hidden $visible ")
    //TODO - Dodać funkcjonalność
}
//@Preview(showBackground = true)
//@Composable
//fun MainScreenPreview(){
//    val padding = PaddingValues(16.dp)
//    TestoAppTheme(true) {
//        MainScreen(values = padding)
//    }
//
//}