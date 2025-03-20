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
import com.napnap.testoapp.data.classes.Main
import com.napnap.testoapp.data.classes.MainString
import com.napnap.testoapp.data.classes.Settings
import com.napnap.testoapp.data.classes.SettingsString
import com.napnap.testoapp.data.classes.baseDirName
import com.napnap.testoapp.data.stores.SettingsStore
import com.napnap.testoapp.ui.screens.info.InfoScreen
import com.napnap.testoapp.ui.screens.main.MainScreen
import com.napnap.testoapp.ui.screens.main.StartDialog
import com.napnap.testoapp.ui.screens.main.handleZipFile
import com.napnap.testoapp.ui.screens.settings.SettingsScreen
import com.napnap.testoapp.ui.theme.TestoAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
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
                val startDialogVisible = remember { mutableStateOf(false) }
                val nameOfItem = remember { mutableStateOf("") }
                createBaseDir(context, baseDirName)
                val navController = rememberNavController()
                val currentRoute by navController.currentBackStackEntryFlow
                    .map { it.destination.route }
                    .collectAsState(initial = Main)
                var header by remember { mutableStateOf(MainString) }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(title = {
                                if(currentRoute == Main){
                                    header = MainString
                                }
                                Text(header,color = MaterialTheme.colorScheme.onPrimary)
                            }, colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary),
                                navigationIcon = {
                                    if (currentRoute != Main) {
                                        IconButton(onClick = {
                                            navController.navigate(Main)
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
                    ) {values -> NavHost(navController = navController, startDestination = Main, builder = {
                            composable(Main){
                                MainScreen(values,startDialogVisible,nameOfItem,getContent)
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
                if(startDialogVisible.value){
                    StartDialog(
                        onDismiss = {
                            startDialogVisible.value = false
                        },
                        nameOfItem = nameOfItem.value,
                    this
                    )
                }
            }
        }

    }

}

fun createBaseDir(context: Context, dirName: String){
    val dir = File(context.filesDir,dirName)
    if (!dir.exists()) {
        if (dir.mkdir()) {
            Log.i("CreateDirectory", "Directory created at: ${dir.absolutePath}")
            File("$baseDirName/history.json").createNewFile()
        } else {
            Log.e("CreateDirectory", "Failed to create directory")

        }
    } else {
        Log.i("CreateDirectory", "Directory already exists: ${dir.absolutePath}")
        if(!File(context.filesDir,"$baseDirName/history.json").createNewFile()){
            Log.i("HistoryJson","File $baseDirName/history.json exists")
        }else{
            Log.i("HistoryJson","File $baseDirName/history.json created")
        }

    }
}
