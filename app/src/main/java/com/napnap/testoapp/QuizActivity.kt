package com.napnap.testoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.napnap.testoapp.data.stores.SettingsStore
import com.napnap.testoapp.ui.theme.TestoAppTheme

class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsStore = SettingsStore()
            TestoAppTheme(settingsStore) {

            }
        }
    }
}
