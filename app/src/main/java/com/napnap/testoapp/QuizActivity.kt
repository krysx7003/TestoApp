@file:OptIn(ExperimentalMaterial3Api::class)

package com.napnap.testoapp

import android.app.Application
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.napnap.testoapp.data.constants.CONTINUE_QUIZ_EXTRA
import com.napnap.testoapp.data.constants.DIR_NAME_EXTRA
import com.napnap.testoapp.data.constants.INFO
import com.napnap.testoapp.data.constants.INFO_STRING
import com.napnap.testoapp.data.constants.QUIZ
import com.napnap.testoapp.data.constants.QUIZ_STRING
import com.napnap.testoapp.data.constants.SEC_TO_HOUR
import com.napnap.testoapp.data.constants.SEC_TO_MIN
import com.napnap.testoapp.data.constants.SETTINGS
import com.napnap.testoapp.data.constants.SETTINGS_STRING
import com.napnap.testoapp.data.stores.SettingsStore
import com.napnap.testoapp.ui.screens.info.InfoScreen
import com.napnap.testoapp.ui.screens.quiz.QuizScreen
import com.napnap.testoapp.ui.screens.quiz.QuizViewModel
import com.napnap.testoapp.ui.screens.settings.SettingsScreen
import com.napnap.testoapp.ui.theme.TestoAppTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsStore = SettingsStore()
            val dirName = intent.extras?.getString(DIR_NAME_EXTRA)
            val continueQuiz = intent.extras?.getBoolean(CONTINUE_QUIZ_EXTRA) != false

            TestoAppTheme(settingsStore) {
                val application = LocalContext.current.applicationContext as Application
                val viewModel = QuizViewModel(application,continueQuiz,dirName.toString())

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuizScaffold(dirName!!,viewModel,settingsStore)
                DisposableEffect(Unit) {
                    onDispose {
                        viewModel.cleanup()
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun QuizScaffold(dirName: String,viewModel: QuizViewModel,settingsStore: SettingsStore){
        val navController = rememberNavController()
        val currentBackStackEntry by navController.currentBackStackEntryFlow
            .collectAsState(initial = navController.currentBackStackEntry)
        val currentRoute = currentBackStackEntry?.destination?.route ?: QUIZ
        var header by remember { mutableStateOf(QUIZ_STRING) }
        val questionList = viewModel.questionList.collectAsState()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(title = {
                    if(currentRoute == QUIZ){
                        header = QUIZ_STRING+dirName.toString().removeSuffix("\\")
                    }
                    Text(header,color = MaterialTheme.colorScheme.onPrimary)
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary),
                    navigationIcon = {
                        if (currentRoute != QUIZ) {
                            BarButton( click = {
                                navController.navigate(QUIZ)
                            },Icons.Filled.ArrowBack,"")
                        }
                    },
                    actions = {
                        if (currentRoute != INFO) {
                            BarButton( click = {
                                navController.navigate(INFO)
                                header = INFO_STRING
                            },Icons.Filled.Info,"")
                        }
                        if (currentRoute != SETTINGS){
                            BarButton( click = {
                                navController.navigate(SETTINGS)
                                header = SETTINGS_STRING
                            },Icons.Filled.Settings,"")
                        }
                    }
                )
            }
        ) {values -> NavHost(navController = navController, startDestination = QUIZ, builder = {
            composable(QUIZ){
                if(questionList.value.isNotEmpty()){
                    QuizScreen(values,dirName.toString(),viewModel)
                }
            }
            composable(SETTINGS){
                SettingsScreen(values,settingsStore)
            }
            composable(INFO) {
                InfoScreen(values)
            }
        })
        }
    }
}

@Composable
fun BarButton(click: () -> Unit, icon: ImageVector, description: String){
    IconButton(onClick = {click}) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

fun Long.toTime(): String {
    val hours = this / SEC_TO_HOUR
    val minutes = (this % SEC_TO_HOUR) / SEC_TO_MIN
    val remainingSeconds = this % SEC_TO_MIN
    return String.format(Locale.GERMANY,"%02d:%02d:%02d", hours, minutes, remainingSeconds)
}

fun String.fromTime():Long{
    val timeParts = this.split(":")

    val hours = timeParts[0].toInt()
    val minutes = timeParts[1].toInt()
    val seconds = timeParts[2].toInt()

    return (hours * SEC_TO_HOUR + minutes * SEC_TO_MIN + seconds).toLong()
}
