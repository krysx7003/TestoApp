@file:OptIn(ExperimentalMaterial3Api::class)

package com.napnap.testoapp.ui.screens.quiz

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.napnap.testoapp.data.classes.Question
import com.napnap.testoapp.data.classes.QuestionFile
import com.napnap.testoapp.data.classes.baseDirName
import com.napnap.testoapp.toTime
import com.napnap.testoapp.ui.theme.Green
import java.io.File

@Composable
fun QuizScreen(values: PaddingValues,dirName:String,viewModel: QuizViewModel,questionFile: QuestionFile){
    val allQuestions = viewModel.allQuestions.collectAsState()
    val completedQuestion = viewModel.completedQuestions.collectAsState()
    val completion = viewModel.completion.collectAsState()
    val timer = viewModel.timer.collectAsState()

    val context = LocalContext.current

    viewModel.loadQuestion(context,dirName,questionFile.name)
    val question = viewModel.question.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(values),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(15.dp),
            colors =  CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                if(question.value?.text?.isNotBlank()!! && allQuestions.value != 1.0){
                    FullHeader(
                        context, question.value!!, dirName,
                        completedQuestion.value, timer.value,
                        allQuestions.value, completion.value
                    )
                }else{
                    InProgressHeader()
                }
            }
        }

        LazyColumn {
            question.value?.let {
                items(it.answers){
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors =  CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                        onClick = {
                            //TODO - Zmiana koloru krawędzi
                        }
                    ) {
                        Text(it.text, modifier = Modifier.padding(15.dp),fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }

}

@Composable
fun FullHeader(context: Context, question: Question, dirName: String, completedQuestion: Double, timer: Long, allQuestions: Double, completion:Float){
    if(question.text.startsWith("[img]") && question.text.endsWith("[/img]")){
        val imageName = question.text.removePrefix("[img]").removeSuffix("[/img]")
        val file = File(context.filesDir,"$baseDirName/$dirName/$imageName")
        if(file.exists()){
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                model = file,
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        }else{
            Log.w("Image","Image ${file.path} not exists")
        }
    }else{
        Text(question.text,fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
    }
    Row(
        modifier =  Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ){
        Text(completedQuestion.toInt().toString(),fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)

        Text(timer.toTime(),fontSize = 30.sp, color = MaterialTheme.colorScheme.onPrimary)

        Text(allQuestions.toInt().toString(),fontSize = 20.sp, color = Green)
    }
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth(),
        progress = completion,
        color = Green,
        trackColor = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
fun InProgressHeader(){
    Text(" ",fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
    Row(
        modifier =  Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ){
        Text("--",fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)

        Text("--:--:--",fontSize = 30.sp, color = MaterialTheme.colorScheme.onPrimary)

        Text("--",fontSize = 20.sp, color = Green)
    }
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth(),
        progress = 0f,
        color = Green,
        trackColor = MaterialTheme.colorScheme.onPrimary
    )
}
