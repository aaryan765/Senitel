package com.aaryan.senitel.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val OPERATOR_NAME = stringPreferencesKey("operator_name")
        val LAST_TARGET = stringPreferencesKey("last_target")
        val LAST_SCAN_TYPE = stringPreferencesKey("last_scan_type")
        val TARGET_HISTORY = stringSetPreferencesKey("target_history")
    }

    val operatorName: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.OPERATOR_NAME] ?: "AIDEN"
        }

    val lastTarget: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LAST_TARGET] ?: "192.168.1.0/24"
        }

    val lastScanType: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LAST_SCAN_TYPE] ?: "HOST DISCOVERY"
        }

    val targetHistory: Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.TARGET_HISTORY]?.toList() ?: emptyList()
        }

    suspend fun updateOperatorName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.OPERATOR_NAME] = name
        }
    }

    suspend fun updateLastTarget(target: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_TARGET] = target
            val currentHistory = preferences[PreferencesKeys.TARGET_HISTORY]?.toMutableList() ?: mutableListOf()
            if (!currentHistory.contains(target)) {
                currentHistory.add(target)
                if (currentHistory.size > 10) {
                    currentHistory.removeAt(0)
                }
                preferences[PreferencesKeys.TARGET_HISTORY] = currentHistory.toSet()
            }
        }
    }

    suspend fun updateLastScanType(scanType: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SCAN_TYPE] = scanType
        }
    }
}
