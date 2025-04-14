package com.napnap.testoapp.ui.screens.quiz

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.napnap.testoapp.data.classes.Answer
import com.napnap.testoapp.data.classes.Question
import com.napnap.testoapp.data.classes.QuestionFile
import com.napnap.testoapp.data.classes.QuizData
import com.napnap.testoapp.data.constants.BASE_DIR_NAME
import com.napnap.testoapp.data.constants.HIST_JSON
import com.napnap.testoapp.data.constants.INITIAL_MAX_AMOUNT
import com.napnap.testoapp.data.constants.MS_TO_SEC
import com.napnap.testoapp.data.constants.SAVE_JSON
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
import kotlin.random.Random

class QuizViewModel(application: Application,continueQuiz:Boolean,dirName: String) : AndroidViewModel(application){
    private val _questionList = MutableStateFlow<List<QuestionFile>>(emptyList())
    val questionList = _questionList.asStateFlow()

    private val _completedQuestions = MutableStateFlow(0.0)
    val completedQuestions = _completedQuestions

    private val _allQuestions = MutableStateFlow(1.0)
    val allQuestions = _allQuestions

    private val _completion = MutableStateFlow(0.0f)
    val completion = _completion

    private val _timer = MutableStateFlow(0L)
    val timer = _timer.asStateFlow()

    private var _question = MutableStateFlow<Question?>(null)
    val question = _question

    private var _questionFile = MutableStateFlow<QuestionFile?>(null)
    val questionFile = _questionFile

    private var timerJob: Job? = null
    private val globalDirName: String = dirName

    private var repeatAmount: Int = 1
    private var startAmount: Int = 2
    private var maxAmount: Int = INITIAL_MAX_AMOUNT

    init {
        val context = getApplication<Application>()
        viewModelScope.launch {
            val settingsStore = SettingsStore()
            repeatAmount = settingsStore.read("repeatAmount",context).first().toString().toIntOrNull() ?: 1
            startAmount = settingsStore.read("startAmount",context).first().toString().toIntOrNull() ?: 2
            maxAmount = settingsStore.read("maxAmount",context).first().toString().toIntOrNull() ?: INITIAL_MAX_AMOUNT
            if(continueQuiz){
                loadTime(context)
            }
            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                while (true){
                    delay(MS_TO_SEC)
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
        val jsonFile = context.filesDir.resolve("$BASE_DIR_NAME/$HIST_JSON")
        if(jsonFile.exists()) {
            val jsonString = jsonFile.bufferedReader().use { it.readText() }
            if (jsonString.isNotEmpty()) {
                val data : List<QuizData> = Gson().fromJson(jsonString, object : TypeToken<List<QuizData>>() {}.type)
                for(quiz in data){
                  if(quiz.name == globalDirName){
                      _timer.value = quiz.time.fromTime()
                      Log.i("LoadTime","Time in Quiz $globalDirName is ${timer.value.toTime()}")
                      break
                  }
                }
            }
        }
    }

    private fun loadData(context: Context,continueQuiz: Boolean){
        val jsonFile = context.filesDir.resolve("$BASE_DIR_NAME/$globalDirName/$SAVE_JSON")
        if(jsonFile.exists()){
            val jsonString = jsonFile.bufferedReader().use{ it.readText() }
            if(jsonString.isNotEmpty()){
                var data : List<QuestionFile> = Gson().fromJson(jsonString, object : TypeToken<List<QuestionFile>>() {}.type)
                if(!continueQuiz){
                    data = reinitJson(data,startAmount)
                }
                _questionList.value = data.shuffled()
                calculateCompletion(data)
                Log.i("LoadHistory","Loaded file $BASE_DIR_NAME/$globalDirName/$SAVE_JSON with ${_questionList.value.size}")
            }else{
                Log.w("LoadHistory","File $BASE_DIR_NAME/$globalDirName/$SAVE_JSON is empty")
            }
        }else{
            Log.w("LoadHistory","File $BASE_DIR_NAME/$globalDirName/$SAVE_JSON doesn't exist")
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
        updateCompletion()
        Log.i("CalcComp","There are $allCount questions and $completedCount of them are completed giving completion rate of $completion")
    }
    fun updateSavedState(){
        val context = getApplication<Application>()
        writeJson(
            context,
            globalDirName,
            questionList.value.toMutableList()
        )
        findAndDelete(context, File(context.filesDir,"$BASE_DIR_NAME/$globalDirName"))
        appendJson(context,globalDirName,
            QuizData(globalDirName,completion.value,timer.value.toTime(),
                LocalDateTime.now().toString())
        )
        Log.i("UpdatedState","State of Quiz $globalDirName")
    }
    fun cleanup(){
        timerJob?.cancel()
        updateSavedState()
    }

    fun loadQuestion(context: Context,dirName: String){
        val nextQuestion = Random.nextInt(0,_questionList.value.size)
        _questionFile.value = _questionList.value[nextQuestion]
        val fileName = _questionFile.value?.name
        Log.i("QuizViewModel","Loading Question $fileName")
        val lineList = ArrayList<String>()
        File(context.filesDir,"$BASE_DIR_NAME/$dirName/$fileName").useLines { lines -> lines.forEach { lineList.add(it) }}
        val correctAnswer = lineList[0].removePrefix("X").toList()
        lineList.removeAt(0)
        val questionText = lineList[0].trim()
        lineList.removeAt(0)
        val answerList = ArrayList<Answer>()
        Log.i("QuizViewModel","Question has ${lineList.size} answers")
        for(i in lineList.indices){
            if(correctAnswer.size > i){
                val isCorrect = correctAnswer[i] == '1'
                answerList.add(Answer(isCorrect,lineList[i]))
            }
        }
        _question.value = Question(questionText,answerList.shuffled())
    }

    fun updateCompletion(){
        if(_completedQuestions.value == 0.0){
            _completion.value = 0.0f
        }else{
            _completion.value = (_completedQuestions.value/_allQuestions.value).toFloat()
        }
    }

    fun updateCompletedQuestions(value: Double){
        _completedQuestions.value += value
        updateCompletion()
    }

    fun incQuestion(name: String){
        _questionList.value = _questionList.value.map { questionFile ->
            if (questionFile.name == name) {
                var repeatAmount = questionFile.repeat + repeatAmount
                if(repeatAmount>maxAmount){
                    repeatAmount = maxAmount
                }
                val updated = questionFile.copy(repeat = repeatAmount )
                if(_questionFile.value?.name == name) {
                    _questionFile.value = updated
                }
                updated
            } else {
                questionFile
            }
        }
    }

    fun decQuestion(name: String){
        _questionList.value = _questionList.value.mapNotNull { questionFile ->
            if (questionFile.name == name) {
                val repeatAmount = questionFile.repeat - 1
                if (repeatAmount > 0) {
                    val updated = questionFile.copy(repeat = repeatAmount )
                    if(_questionFile.value?.name == name) {
                        _questionFile.value = updated
                    }
                    updated
                } else {
                    val updated = questionFile.copy(repeat = 0 )
                    if(_questionFile.value?.name == name) {
                        _questionFile.value = updated
                    }
                    updateCompletedQuestions(1.0)
                    null
                }
            } else {
                questionFile
            }
        }
    }
}
