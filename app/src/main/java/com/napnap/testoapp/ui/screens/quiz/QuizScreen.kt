@file:OptIn(ExperimentalMaterial3Api::class)

package com.napnap.testoapp.ui.screens.quiz

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.napnap.testoapp.data.classes.Answer
import com.napnap.testoapp.data.classes.Question
import com.napnap.testoapp.data.classes.QuestionFile
import com.napnap.testoapp.data.classes.baseDirName
import com.napnap.testoapp.toTime
import com.napnap.testoapp.ui.theme.Green
import com.napnap.testoapp.ui.theme.Red
import com.napnap.testoapp.ui.theme.SoftYellow
import com.napnap.testoapp.ui.theme.White
import java.io.File

@Composable
fun QuizScreen(values: PaddingValues,dirName:String,viewModel: QuizViewModel){
    val allQuestions = viewModel.allQuestions.collectAsState()
    val completedQuestion = viewModel.completedQuestions.collectAsState()
    val completion = viewModel.completion.collectAsState()
    val timer = viewModel.timer.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.loadQuestion(context,dirName)
    }
    val question = viewModel.question.collectAsState()
    val questionFile = viewModel.questionFile.collectAsState()

    val answered = remember { mutableStateOf(false) }
    val answerChosen = remember { mutableStateOf<List<Boolean>>(emptyList()) }
    val answerColor = remember { mutableStateOf<List<Color>>(emptyList()) }
    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(question.value) {
        val size = question.value?.answers?.size ?: 0
        answerChosen.value = List(size) { false }
        answerColor.value = List(size){ primaryColor }
        answered.value = false
    }
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
                question.value?.let {
                    FullHeader(
                        context, question.value!!, dirName,
                        completedQuestion.value, timer.value,
                        allQuestions.value, completion.value,questionFile.value!!
                    )
                } ?: InProgressHeader()
            }
        }
        Box(
           modifier = Modifier.fillMaxSize()
        ){
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ){
                question.value?.let {
                    itemsIndexed(it.answers) {index,answer ->
                        val isChosen = answerChosen.value.getOrNull(index) == true
                        val cardColor = answerColor.value.getOrNull(index) ?: primaryColor
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            shape = RoundedCornerShape(15.dp),
                            colors =  CardDefaults.cardColors(
                                containerColor = cardColor,
                            ),
                            onClick = {
                                if(!answered.value){
                                    val currList = answerChosen.value.toMutableList()
                                    if(index < currList.size){
                                        currList[index] = !currList[index]
                                        answerChosen.value = currList
                                    }
                                }
                            }
                        ) {
                            Row{
                                Text(answer.text, modifier = Modifier.padding(15.dp).weight(1.2f),fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
                                if(answered.value){
                                    val currColorList = answerColor.value.toMutableList()
                                    when {
                                        isChosen && answer.correct -> {
                                            currColorList[index] = Green
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier
                                                    .align(Alignment.CenterVertically)
                                                    .weight(1f)
                                                    .size(30.dp)
                                            )
                                            Log.i("Answer", "Answer $index answered correctly")
                                        }
                                        !isChosen && answer.correct -> {
                                            currColorList[index] = SoftYellow
                                            Icon(
                                                imageVector = questionMark,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier
                                                    .align(Alignment.CenterVertically)
                                                    .weight(1f)
                                                    .size(30.dp)
                                            )
                                            Log.i("Answer", "Answer $index not answered")
                                        }
                                         isChosen && !answer.correct -> {
                                            currColorList[index] = Red
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier
                                                    .align(Alignment.CenterVertically)
                                                    .weight(1f)
                                                    .size(30.dp)
                                            )
                                            Log.i("Answer", "Answer $index answered incorrectly")
                                        }
                                    }
                                    answerColor.value = currColorList
                                }else if(isChosen){
                                    Icon(
                                        imageVector = questionMark,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .weight(1f)
                                            .size(30.dp)
                                    )
                                }
                            }

                        }
                        if(answer == question.value?.answers?.last()){
                            Spacer(modifier = Modifier.padding(bottom = 70.dp))
                        }
                    }
                }
            }
            Button(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(70.dp)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(containerColor = Green),
                onClick = {
                    if(!answered.value){
                        if(answeredCorrectly(answerChosen.value,question.value?.answers!!)){
                            viewModel.decQuestion(questionFile.value?.name!!)
                            Log.i("Answer","Answered correctly")
                        }else{
                            viewModel.incQuestion(questionFile.value?.name!!)
                            Log.i("Answer","Answered incorrectly")
                        }
                        answered.value = true
                    }else{
                        Log.i("Answer","Loading next question ${answered.value}")
                        viewModel.updateSavedState()
                        viewModel.loadQuestion(context,dirName)
                        answered.value = false
                    }
                },
            ) {
                var icon = if(!answered.value){
                    Icons.Filled.PlayArrow
                }else{
                    Icons.Filled.ArrowForward
                }
                Icon(
                    imageVector = icon,
                    contentDescription = "",
                    tint = White,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}

@Composable
fun FullHeader(context: Context, question: Question, dirName: String, completedQuestion: Double, timer: Long, allQuestions: Double, completion:Float,questionFile: QuestionFile){
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
    Text("Pytanie: ${questionFile.name} Liczba powtórzeń: ${questionFile.repeat} ",fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
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

fun answeredCorrectly(chosenAnswers: List<Boolean>, correctAnswers: List<Answer>): Boolean{
    for(i in correctAnswers.indices){
        if(chosenAnswers[i] != correctAnswers[i].correct){
            return false
        }
    }
    return true
}