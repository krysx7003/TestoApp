package com.napnap.testoapp.ui.screens.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.napnap.testoapp.data.classes.Quiz
import com.napnap.testoapp.data.classes.baseDirName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(context: Context) : ViewModel(){
    private val _quizHistory = MutableStateFlow<List<Quiz>>(emptyList())
    val quizHistory = _quizHistory.asStateFlow()
    init {
        loadData(context)
    }

    private fun loadData(context: Context){
        viewModelScope.launch {
            val jsonFile = context.filesDir.resolve("$baseDirName/history.json")
            if(jsonFile.exists()){
                val jsonString = jsonFile.bufferedReader().use{ it.readText() }
                if(jsonString.isNotEmpty()){
                    val data : List<Quiz> = Gson().fromJson(jsonString, object : TypeToken<List<Quiz>>() {}.type)
                    _quizHistory.value = data
                    Log.i("LoadHistory","Loaded file $baseDirName/history.json with ${_quizHistory.value.size}")
                }else{
                    Log.w("LoadHistory","File $baseDirName/history.json is empty")
                }

            }else{
                Log.w("LoadHistory","File $baseDirName/history.json doesn't exist")
            }
            _quizHistory.value = listOf(Quiz("test",21.37,"0:00"))
        }
    }
}