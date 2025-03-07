@file:OptIn(ExperimentalMaterial3Api::class)

package com.napnap.testoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.napnap.testoapp.data.classes.Info
import com.napnap.testoapp.data.classes.Main
import com.napnap.testoapp.data.classes.Settings
import com.napnap.testoapp.data.stores.SettingsStore
import com.napnap.testoapp.ui.theme.TestoAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsStore = SettingsStore()
            TestoAppTheme(settingsStore) {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(title = {}, colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            ), actions = {
                                //TODO - Schowaj przyciski gdy jesteś w odpowiednim miejscu
                                IconButton(onClick = {
                                    if (navController.currentBackStackEntry?.destination?.route != Info){
                                        navController.navigate(Info)
                                    }else{
                                        navController.navigate(Main)
                                    }
                                }){ Icon(imageVector = Icons.Filled.Info, contentDescription = "",tint = MaterialTheme.colorScheme.onPrimary)}
                                IconButton(onClick = {
                                    if (navController.currentBackStackEntry?.destination?.route != Settings){
                                        navController.navigate(Settings)
                                    }else{
                                        navController.navigate(Main)
                                    }
                                }){ Icon(imageVector = Icons.Filled.Settings, contentDescription = "",tint = MaterialTheme.colorScheme.onPrimary) }
                            } )
                        }
                    ) {values -> NavHost(navController = navController, startDestination = Main, builder = {
                            composable(Main){
                                MainScreen(values)
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