@file:OptIn(ExperimentalMaterial3Api::class)

package com.napnap.testoapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.napnap.testoapp.data.stores.SettingsStore
import com.napnap.testoapp.ui.theme.TestoAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestoAppTheme(true) {
                val navController = rememberNavController()
                val context = LocalContext.current.applicationContext
                val settingsStore = SettingsStore()
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
                            ), actions = { IconButton(onClick = {
                                    if (navController.currentBackStackEntry?.destination?.route != "SettingsScreen"){
                                        navController.navigate("SettingsScreen")
                                    }else{
                                        Toast.makeText(context,"Ustawienia są otwarte",Toast.LENGTH_SHORT).show()
                                    }

                                }){
                                    Icon(imageVector = Icons.Filled.Settings, contentDescription = "",tint = MaterialTheme.colorScheme.onPrimary)
                            } } )
                        }
                    ) {values -> NavHost(navController = navController, startDestination = "MainScreen", builder = {
                            composable("MainScreen"){
                                MainScreen(values)
                            }
                            composable("SettingsScreen"){
                                SettingsScreen(values,settingsStore)
                            }
                        })
                    }

                }
            }
        }
    }
}