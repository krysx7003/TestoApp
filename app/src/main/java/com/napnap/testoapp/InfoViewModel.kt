package com.napnap.testoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InfoViewModel : ViewModel() {
    private val _changeLog = MutableStateFlow<List<String>>(emptyList())

    val changeLog = _changeLog.asStateFlow()

    init{
        loadData()
    }

    private fun loadData(){
        viewModelScope.launch {
            //TODO - To powinno być w jakiś sposób ładowane
            _changeLog.value = listOf(
                "First bullet",
                "Second bullet ... which is awfully long but that's not a problem",
                "Third bullet ",
            )
        }
    }
}