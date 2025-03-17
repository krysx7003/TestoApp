package com.napnap.testoapp.ui.screens.main

import Folder
import Github
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun LoadDialog(
    onDismiss:()->Unit
){
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Text("Wczytaj z:",
                    fontSize = 25.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f),
                        onClick = { loadFromGithub() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    ) {
                        Column (
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ){
                            Icon(
                                modifier = Modifier
                                    .size(40.dp),
                                imageVector = Github,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Text("GitHub",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    Button(
                        modifier = Modifier
                            .weight(1f),
                        onClick = { loadFromDevice() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(40.dp),
                                imageVector = Folder,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Text("Pamięć",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                Button(
                    onClick = { onDismiss() }
                ) {
                    Text("Anuluj",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
/*
    * TODO - Po wybraniu opcji rozpakowuje odpowiedni plik
    *  Zawartość zostaje zapisana w nowym folderze (Nazwa??)
    *  Jeśli nazwy się powtarzają usuwamy stary folder i tworzymy nowy
    *  Nowy quiz zostaje automatycznie odpalony
    * */
fun loadFromDevice(){
    Log.i("LoadQuiz","Loading Quiz from device memory ")
}

fun loadFromGithub(){
    Log.i("LoadQuiz","Loading Quiz from GitHub ")
}

//TODO - Rozpakuj zip

//startQuiz(nameOfItem,context,false)
