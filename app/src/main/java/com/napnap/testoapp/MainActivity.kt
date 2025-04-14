@file:OptIn(ExperimentalMaterial3Api::class)

package com.napnap.testoapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.napnap.testoapp.data.constants.BASE_DIR_NAME
import com.napnap.testoapp.data.constants.INFO
import com.napnap.testoapp.data.constants.INFO_STRING
import com.napnap.testoapp.data.constants.MAIN
import com.napnap.testoapp.data.constants.MAIN_STRING
import com.napnap.testoapp.data.constants.QUIZ
import com.napnap.testoapp.data.constants.QUIZ_STRING
import com.napnap.testoapp.data.constants.SETTINGS
import com.napnap.testoapp.data.constants.SETTINGS_STRING
import com.napnap.testoapp.data.stores.SettingsStore
import com.napnap.testoapp.ui.screens.info.InfoScreen
import com.napnap.testoapp.ui.screens.main.MainScreen
import com.napnap.testoapp.ui.screens.main.StartDialog
import com.napnap.testoapp.ui.screens.main.handleZipFile
import com.napnap.testoapp.ui.screens.settings.SettingsScreen
import com.napnap.testoapp.ui.theme.TestoAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            CoroutineScope(Dispatchers.Main).launch {
                handleZipFile(it, applicationContext)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsStore = SettingsStore()
            TestoAppTheme(settingsStore) {
                val context = LocalContext.current.applicationContext
                createBaseDir(context, BASE_DIR_NAME)
                val startDialogVisible = remember { mutableStateOf(false) }
                val nameOfItem = remember { mutableStateOf("") }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScaffold(startDialogVisible,nameOfItem,settingsStore)
                    if(startDialogVisible.value){
                        StartDialog(
                            onDismiss = {
                                startDialogVisible.value = false
                            }, nameOfItem = nameOfItem.value, this
                        )
                    }
                }
            }

        }

    }

    @Composable
    fun MainScaffold( startDialogVisible: MutableState<Boolean>, nameOfItem: MutableState<String>,
                        settingsStore: SettingsStore){
        val navController = rememberNavController()
        val currentBackStackEntry by navController.currentBackStackEntryFlow
            .collectAsState(initial = navController.currentBackStackEntry)
        val currentRoute = currentBackStackEntry?.destination?.route ?: QUIZ
        var header by remember { mutableStateOf(QUIZ_STRING) }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(title = {
                    if(currentRoute == MAIN){
                        header = MAIN_STRING
                    }
                    Text(header,color = MaterialTheme.colorScheme.onPrimary)
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary),
                    navigationIcon = {
                        if (currentRoute != MAIN) {
                            BarButton( click = {
                                navController.navigate(MAIN)
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
        ) {values -> NavHost(navController = navController, startDestination = MAIN, builder = {
                composable(MAIN){
                    MainScreen(values,startDialogVisible,nameOfItem,getContent)
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


fun createBaseDir(context: Context, dirName: String){
    val dir = File(context.filesDir,dirName)
    if (!dir.exists()) {
        if (dir.mkdir()) {
            Log.i("CreateDirectory", "Directory created at: ${dir.absolutePath}")
            File("$BASE_DIR_NAME/history.json").createNewFile()
        } else {
            Log.e("CreateDirectory", "Failed to create directory")

        }
    } else {
        Log.i("CreateDirectory", "Directory already exists: ${dir.absolutePath}")
        if(!File(context.filesDir,"$BASE_DIR_NAME/history.json").createNewFile()){
            Log.i("HistoryJson","File $BASE_DIR_NAME/history.json exists")
        }else{
            Log.i("HistoryJson","File $BASE_DIR_NAME/history.json created")
        }

    }
}
