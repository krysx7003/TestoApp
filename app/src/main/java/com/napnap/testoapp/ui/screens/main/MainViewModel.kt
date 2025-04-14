package com.napnap.testoapp.ui.screens.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.napnap.testoapp.data.classes.QuizData
import com.napnap.testoapp.data.classes.BASE_DIR_NAME
import com.napnap.testoapp.data.classes.HIST_JSON
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainViewModel(context: Context) : ViewModel(){
    private val _quizDataHistory = MutableStateFlow<List<QuizData>>(emptyList())
    val quizHistory = _quizDataHistory.asStateFlow()
    init {
        loadData(context)
    }

    private fun loadData(context: Context){
        viewModelScope.launch {
            val jsonFile = context.filesDir.resolve("$BASE_DIR_NAME/$HIST_JSON")
            if(jsonFile.exists()){
                val jsonString = jsonFile.bufferedReader().use{ it.readText() }
                if(jsonString.isNotEmpty()){
                    val data : List<QuizData> = Gson().fromJson(jsonString, object : TypeToken<List<QuizData>>() {}.type)
                    _quizDataHistory.value = sortQuizData(data)
                    Log.i("LoadHistory","Loaded file $BASE_DIR_NAME/$HIST_JSON with ${_quizDataHistory.value.size}")
                }else{
                    Log.w("LoadHistory","File $BASE_DIR_NAME/$HIST_JSON is empty")
                }

            }else{
                Log.w("LoadHistory","File $BASE_DIR_NAME/$HIST_JSON doesn't exist")
            }
        }
    }

    private fun sortQuizData(data:List<QuizData>):List<QuizData>{
        return data.sortedByDescending {
            LocalDateTime.parse(it.date)
        }
    }
}
