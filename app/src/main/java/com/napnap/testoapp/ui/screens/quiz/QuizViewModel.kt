package com.napnap.testoapp.ui.screens.quiz

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.napnap.testoapp.data.classes.QuestionFile
import com.napnap.testoapp.data.classes.baseDirName
import com.napnap.testoapp.data.classes.saveJson
import com.napnap.testoapp.data.stores.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QuizViewModel(context: Context,continueQuiz:Boolean) : ViewModel(){
    private val _questionList = MutableStateFlow<List<QuestionFile>>(emptyList())
    val questionList = _questionList.asStateFlow()
    private val _completion = MutableStateFlow(0)
    val completion = _completion
    init {
        loadData(context,continueQuiz)
    }
    private fun loadData(context: Context,continueQuiz: Boolean){
        viewModelScope.launch {
            val settingsStore = SettingsStore()
            val startAmount = settingsStore.read("startAmount",context).first().toString().toIntOrNull() ?: 2
            val jsonFile = context.filesDir.resolve("$baseDirName/$saveJson")
            if(jsonFile.exists()){
                val jsonString = jsonFile.bufferedReader().use{ it.readText() }
                if(jsonString.isNotEmpty()){
                    var data : List<QuestionFile> = Gson().fromJson(jsonString, object : TypeToken<List<QuestionFile>>() {}.type)
                    _questionList.value = data
                    if(continueQuiz){
                        data = reinitJson(data,startAmount)
                    }
                    calculateCompletion(data)
                    Log.i("LoadHistory","Loaded file $baseDirName/$saveJson with ${_questionList.value.size}")
                }else{
                    Log.w("LoadHistory","File $baseDirName/$saveJson is empty")
                }

            }else{
                Log.w("LoadHistory","File $baseDirName/$saveJson doesn't exist")
            }
        }
    }

    private fun reinitJson(data: List<QuestionFile>,startAmount:Int):List<QuestionFile>{
        Log.i("ReinitJson","All questions set to repeat $startAmount times")
        return data.map { it.copy(repeat = startAmount) }
    }

    fun writeJson(context: Context,zipName: String,questionFileList: List<QuestionFile> ){
        val questionJson = Gson().toJson(questionFileList)
        val jsonFileQ = context.filesDir.resolve("$baseDirName/$zipName/$saveJson")
        jsonFileQ.writeText(questionJson)
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
        _completion.value = completedCount/allCount
        Log.i("CalcComp","There are $allCount questions and $completedCount of them are completed giving completion rate of $completion")
    }
}