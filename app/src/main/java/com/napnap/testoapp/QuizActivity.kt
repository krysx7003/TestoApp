package com.napnap.testoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.napnap.testoapp.data.classes.Info
import com.napnap.testoapp.data.classes.InfoString
import com.napnap.testoapp.data.classes.Quiz
import com.napnap.testoapp.data.classes.QuizString
import com.napnap.testoapp.data.classes.Settings
import com.napnap.testoapp.data.classes.SettingsString
import com.napnap.testoapp.data.classes.continueExtra
import com.napnap.testoapp.data.classes.dirNameExtra
import com.napnap.testoapp.data.stores.SettingsStore
import com.napnap.testoapp.ui.screens.info.InfoScreen
import com.napnap.testoapp.ui.screens.quiz.QuizScreen
import com.napnap.testoapp.ui.screens.quiz.QuizViewModel
import com.napnap.testoapp.ui.screens.settings.SettingsScreen
import com.napnap.testoapp.ui.theme.TestoAppTheme
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsStore = SettingsStore()
            val dirName = intent.extras?.getString(dirNameExtra)
            val continueQuiz = intent.extras?.getBoolean(continueExtra) ?: true

            TestoAppTheme(settingsStore) {
                val localContext = LocalContext.current
                val navController = rememberNavController()
                val currentRoute by navController.currentBackStackEntryFlow
                    .map { it.destination.route }
                    .collectAsState(initial = Quiz)
                var header by remember { mutableStateOf(QuizString) }
                val viewModel = QuizViewModel(localContext,continueQuiz,dirName.toString())

                val questionList = viewModel.questionList.collectAsState()
                val completion = viewModel.completion.collectAsState()
                val timer = viewModel.timer.collectAsState()


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(title = {
                                if(currentRoute == Quiz){
                                    header = QuizString+dirName.toString().removeSuffix("\\")
                                }
                                Text(header,color = MaterialTheme.colorScheme.onPrimary)
                            }, colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary),
                                navigationIcon = {
                                    if (currentRoute != Quiz) {
                                        IconButton(onClick = {
                                            navController.navigate(Quiz)
                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.ArrowBack,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    if (currentRoute != Info) {
                                        IconButton(onClick = {
                                            navController.navigate(Info)
                                            header = InfoString
                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.Info,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                    }
                                    if (currentRoute != Settings){
                                        IconButton(onClick = {
                                            navController.navigate(Settings)
                                            header = SettingsString
                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.Settings,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    ) {values -> NavHost(navController = navController, startDestination = Quiz, builder = {
                        composable(Quiz){
                            QuizScreen(values)
                        }
                        composable(Settings){
                            SettingsScreen(values,settingsStore)
                        }
                        composable(Info) {
                            InfoScreen(values)
                        }
                    })
                    }

                }
            }
        }
    }
}
fun Long.formatTime(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val remainingSeconds = this % 60
    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}
