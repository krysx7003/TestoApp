@file:OptIn(ExperimentalMaterial3Api::class)

package com.napnap.testoapp.ui.screens.quiz

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.napnap.testoapp.data.classes.Answer
import com.napnap.testoapp.data.classes.Question
import com.napnap.testoapp.data.classes.QuestionFile
import com.napnap.testoapp.data.classes.baseDirName
import com.napnap.testoapp.toTime
import com.napnap.testoapp.ui.theme.Green
import com.napnap.testoapp.ui.theme.Red
import java.io.File

@Composable
fun QuizScreen(values: PaddingValues,dirName:String,viewModel: QuizViewModel,questionFile: QuestionFile){
    val allQuestions = viewModel.allQuestions.collectAsState()
    val completedQuestion = viewModel.completedQuestions.collectAsState()
    val completion = viewModel.completion.collectAsState()
    val timer = viewModel.timer.collectAsState()

    val context = LocalContext.current
    //TODO - Do viewModelu??
    val question = loadQuestion(context,dirName,questionFile.name)

    //TODO - Poprawić polskie znaki
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
                Text(question.text,fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
                Row(
                    modifier =  Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ){
                    Text(completedQuestion.value.toInt().toString(),fontSize = 20.sp, color = Red)

                    Text(timer.value.toTime(),fontSize = 30.sp, color = MaterialTheme.colorScheme.onPrimary)

                    Text(allQuestions.value.toInt().toString(),fontSize = 20.sp, color = Green)
                }
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(),
                    progress = completion.value,
                    color = Green,
                    trackColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        LazyColumn {
            items(question.answers){
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

fun loadQuestion(context: Context,dirName: String,fileName: String): Question {
    val lineList = ArrayList<String>()
    File(context.filesDir,"$baseDirName/$dirName/$fileName").useLines { lines -> lines.forEach { lineList.add(it) }}
    val correctAnswer = lineList[0].removePrefix("X").toList()
    lineList.removeAt(0)
    val questionText = lineList[0]
    lineList.removeAt(0)
    val answerList = ArrayList<Answer>()
    for(i in lineList.indices){
        val isCorrect = correctAnswer[i] == '1'
        answerList.add(Answer(isCorrect,lineList[i]))
    }
    return Question(questionText,answerList.shuffled())
}