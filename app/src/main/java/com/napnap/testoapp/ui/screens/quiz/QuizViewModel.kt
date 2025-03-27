package com.napnap.testoapp.ui.screens.quiz

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.napnap.testoapp.data.classes.QuestionFile
import com.napnap.testoapp.data.classes.QuizData
import com.napnap.testoapp.data.classes.baseDirName
import com.napnap.testoapp.data.classes.histJson
import com.napnap.testoapp.data.classes.saveJson
import com.napnap.testoapp.data.stores.SettingsStore
import com.napnap.testoapp.fromTime
import com.napnap.testoapp.toTime
import com.napnap.testoapp.ui.screens.main.appendJson
import com.napnap.testoapp.ui.screens.main.findAndDelete
import com.napnap.testoapp.ui.screens.main.writeJson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime

class QuizViewModel(context: Context,continueQuiz:Boolean,dirName: String) : ViewModel(){
    private val _questionList = MutableStateFlow<List<QuestionFile>>(emptyList())
    val questionList = _questionList.asStateFlow()

    private val _completedQuestions = MutableStateFlow(1.0)
    val completedQuestions = _completedQuestions

    private val _allQuestions = MutableStateFlow(1.0)
    val allQuestions = _allQuestions

    private val _completion = MutableStateFlow((_allQuestions.value/_completedQuestions.value).toFloat())
    val completion = _completion

    private val _timer = MutableStateFlow(0L)
    val timer = _timer.asStateFlow()

    private var timerJob: Job? = null
    init {
        viewModelScope.launch {
            if(continueQuiz){
                loadTime(context, dirName)
            }
            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                while (true){
                    delay(1000)
                    _timer.value++
                }
            }
            loadData(context,continueQuiz, dirName)
            if(questionList.value.isNotEmpty()){
                writeJson(
                    context,
                    dirName,
                    questionList.value.toMutableList()
                )
                findAndDelete(context, File(context.filesDir,"$baseDirName/$dirName"))
                appendJson(context,dirName,
                    QuizData(dirName,completion.value,timer.value.toTime(),
                        LocalDateTime.now().toString())
                )
            }
        }
    }

    private fun loadTime(context: Context,dirName: String) {
        val jsonFile = context.filesDir.resolve("$baseDirName/$dirName/$histJson")
        if(jsonFile.exists()) {
            val jsonString = jsonFile.bufferedReader().use { it.readText() }
            if (jsonString.isNotEmpty()) {
                val data : List<QuizData> = Gson().fromJson(jsonString, object : TypeToken<List<QuizData>>() {}.type)
                for(quiz in data){
                  if(quiz.name == dirName){
                      _timer.value = quiz.time.fromTime()
                      break
                  }
                }
            }
        }
    }

    private suspend fun loadData(context: Context,continueQuiz: Boolean,dirName:String){
        val settingsStore = SettingsStore()
        val startAmount = settingsStore.read("startAmount",context).first().toString().toIntOrNull() ?: 2
        val jsonFile = context.filesDir.resolve("$baseDirName/$dirName/$saveJson")
        if(jsonFile.exists()){
            val jsonString = jsonFile.bufferedReader().use{ it.readText() }
            if(jsonString.isNotEmpty()){
                var data : List<QuestionFile> = Gson().fromJson(jsonString, object : TypeToken<List<QuestionFile>>() {}.type)
                if(!continueQuiz){
                    data = reinitJson(data,startAmount)
                }
                _questionList.value = data.shuffled()
                calculateCompletion(data)
                Log.i("LoadHistory","Loaded file $baseDirName/$dirName/$saveJson with ${_questionList.value.size}")
            }else{
                Log.w("LoadHistory","File $baseDirName/$dirName/$saveJson is empty")
            }
        }else{
            Log.w("LoadHistory","File $baseDirName/$dirName/$saveJson doesn't exist")
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        //TODO - Zapisz do save.json
        //TODO - Zapisz do hist.json
    }

    private fun reinitJson(data: List<QuestionFile>,startAmount:Int):List<QuestionFile>{
        Log.i("ReinitJson","All questions set to repeat $startAmount times")
        return data.map { it.copy(repeat = startAmount) }
    }

    private fun calculateCompletion(data:List<QuestionFile>){
        var completedCount = 0
        var allCount = 0
        for(item in data){
            if(item.repeat == 0){
                completedCount++
            }
            allCount++
        }
        _completedQuestions.value = (completedCount).toDouble()
        _allQuestions.value = (allCount).toDouble()
        Log.i("CalcComp","There are $allCount questions and $completedCount of them are completed giving completion rate of $completion")
    }
}