package com.napnap.testoapp.ui.screens.main

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun StartDialog(onDismiss:()->Unit,
                nameOfItem: String,
                context: Context
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
                .fillMaxWidth(0.95f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Text("Quiz $nameOfItem:",
                    fontSize = 25.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                ) {
                    DialogButton(
                        nameOfItem,context,onDismiss,
                        "Wznów",Icons.Filled.PlayArrow,"",
                        true,
                        Modifier
                            .weight(1f)
                    )
                    DialogButton(
                        nameOfItem,context,onDismiss,
                        "Od początku",Icons.Filled.Refresh,"",
                        false,
                        Modifier
                            .weight(1f)
                    )
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

@Composable
fun DialogButton(nameOfItem: String,
                 context: Context,
                 onDismiss: () -> Unit,
                 text: String,
                 icon: ImageVector,
                 description: String,
                 continueQuiz: Boolean,
                 modifier: Modifier
){
    Button(
        modifier = modifier,
        onClick = {
            startQuiz(nameOfItem,context,continueQuiz)
            onDismiss()
        },
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
                imageVector = icon,
                contentDescription = description,
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(text,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
