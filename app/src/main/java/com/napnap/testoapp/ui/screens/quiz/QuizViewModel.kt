package com.napnap.testoapp.ui.screens.quiz

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
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

class QuizViewModel(application: Application,continueQuiz:Boolean,dirName: String) : AndroidViewModel(application){
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
    private val globalDirName: String = dirName
    init {
        val context = getApplication<Application>()
        viewModelScope.launch {
            if(continueQuiz){
                loadTime(context)
            }
            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                while (true){
                    delay(1000)
                    _timer.value++
                }
            }
            loadData(context,continueQuiz)
            if(questionList.value.isNotEmpty()){
                updateSavedState()
            }
        }
    }

    private fun loadTime(context: Context) {
        val jsonFile = context.filesDir.resolve("$baseDirName/$histJson")
        if(jsonFile.exists()) {
            val jsonString = jsonFile.bufferedReader().use { it.readText() }
            if (jsonString.isNotEmpty()) {
                val data : List<QuizData> = Gson().fromJson(jsonString, object : TypeToken<List<QuizData>>() {}.type)
                for(quiz in data){
                  if(quiz.name == globalDirName){
                      _timer.value = quiz.time.fromTime()
                      break
                  }
                }
            }
        }
    }

    private suspend fun loadData(context: Context,continueQuiz: Boolean){
        val settingsStore = SettingsStore()
        val startAmount = settingsStore.read("startAmount",context).first().toString().toIntOrNull() ?: 2
        val jsonFile = context.filesDir.resolve("$baseDirName/$globalDirName/$saveJson")
        if(jsonFile.exists()){
            val jsonString = jsonFile.bufferedReader().use{ it.readText() }
            if(jsonString.isNotEmpty()){
                var data : List<QuestionFile> = Gson().fromJson(jsonString, object : TypeToken<List<QuestionFile>>() {}.type)
                if(!continueQuiz){
                    data = reinitJson(data,startAmount)
                }
                _questionList.value = data.shuffled()
                calculateCompletion(data)
                Log.i("LoadHistory","Loaded file $baseDirName/$globalDirName/$saveJson with ${_questionList.value.size}")
            }else{
                Log.w("LoadHistory","File $baseDirName/$globalDirName/$saveJson is empty")
            }
        }else{
            Log.w("LoadHistory","File $baseDirName/$globalDirName/$saveJson doesn't exist")
        }
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
    private fun updateSavedState(){
        val context = getApplication<Application>()
        writeJson(
            context,
            globalDirName,
            questionList.value.toMutableList()
        )
        findAndDelete(context, File(context.filesDir,"$baseDirName/$globalDirName"))
        appendJson(context,globalDirName,
            QuizData(globalDirName,completion.value,timer.value.toTime(),
                LocalDateTime.now().toString())
        )
    }
    fun cleanup(){
        timerJob?.cancel()
        updateSavedState()
    }

}