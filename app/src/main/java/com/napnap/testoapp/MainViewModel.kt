package com.napnap.testoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.napnap.testoapp.data.classes.Quiz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel(){
    private val _quizHistory = MutableStateFlow<List<Quiz>>(emptyList())

    val quizHistory = _quizHistory.asStateFlow()

    init {
        loadData()
    }

    private fun loadData(){
        viewModelScope.launch {
            //TODO - To powinno być w jakiś sposób ładowane
            _quizHistory.value = listOf(
                Quiz("Task 1", 23.5, "12:30:45"),
                Quiz("Task 2", 72.3, "08:15:20"),
                Quiz("Task 3", 90.0, "14:05:10"),
                Quiz("Task 4", 49.0, "10:40:05"),
                Quiz("Task 5", 95.6, "09:30:55")
            )
        }
    }
}