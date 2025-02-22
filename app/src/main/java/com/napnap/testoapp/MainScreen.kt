package com.napnap.testoapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.napnap.testoapp.ui.theme.TestoAppTheme

@Composable
fun MainScreen(values: PaddingValues){
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
                .padding(horizontal = 50.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(60.dp)
                    .padding(vertical = 0.dp)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ){ Text("Historia",
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge,)
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 0.dp)
                    .background(MaterialTheme.colorScheme.primary),
            ) {
                items(5) { index ->
                    Text(text = "Item: $index")
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

}

fun continueQuiz(){

}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview(){
    val padding = PaddingValues(16.dp)
    TestoAppTheme(true) {
        MainScreen(values = padding)
    }

}