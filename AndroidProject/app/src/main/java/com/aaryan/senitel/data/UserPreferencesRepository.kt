package com.aaryan.senitel.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val OPERATOR_NAME = stringPreferencesKey("operator_name")
    }

    val operatorName: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.OPERATOR_NAME] ?: "AIDEN"
        }

    suspend fun updateOperatorName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.OPERATOR_NAME] = name
        }
    }
}
