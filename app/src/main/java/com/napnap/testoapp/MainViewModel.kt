package com.napnap.testoapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.napnap.testoapp.data.classes.Quiz
import com.napnap.testoapp.data.classes.baseDirName
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(context: Context) : ViewModel(){
    private val _quizHistory = MutableStateFlow<List<Quiz>>(emptyList())
    val quizHistory = _quizHistory.asStateFlow()
    init {
        loadData(context)
    }

    private fun loadData(context: Context){
        viewModelScope.launch {
            val dir = File(context.filesDir, baseDirName)
            if(dir.exists() && dir.isDirectory){
                val directories = dir.listFiles()?.filter { it.exists() && it.isDirectory } ?: emptyList()
            }


            _quizHistory.value = listOf(
                Quiz("Task 1", 23.5, "2:30"),
                Quiz("Task 2", 72.3, "8:15"),
                Quiz("Task 3", 90.0, "4:05"),
                Quiz("Task 4", 49.0, "0:40"),
                Quiz("Task 5", 95.6, "9:30")
            )
        }
    }
}