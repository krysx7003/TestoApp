package com.napnap.testoapp.data.stores

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsStore {
    suspend fun save(key:String,value:String,context: Context){
        val dataStoreKey = stringPreferencesKey(key)
        context.dataStore.edit{ settings->
            settings[dataStoreKey] = value
        }
    }
    fun read(key:String,context: Context): Flow<String?> {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = context.dataStore.data
            .map { it[dataStoreKey] }
        return preferences
    }
}