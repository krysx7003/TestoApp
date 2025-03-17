package com.napnap.testoapp.ui.screens.info

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.napnap.testoapp.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InfoViewModel(context: Context) : ViewModel() {
    private val _changeLog = MutableStateFlow<List<String>>(emptyList())

    val changeLog = _changeLog.asStateFlow()

    init{
        loadData(context)
    }

    private fun loadData(context: Context){
        viewModelScope.launch {
            val jsonString = context.resources.openRawResource(R.raw.changelog).bufferedReader().use{ it.readText() }
            _changeLog.value = Gson().fromJson(jsonString, object : TypeToken<List<String>>() {}.type)
        }
    }
}