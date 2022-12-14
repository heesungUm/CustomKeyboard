package com.heesungum.customkeyboard

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first



val Context.datastore : DataStore<Preferences> by  preferencesDataStore(name = "KEYBOARD_DATASTORE")

class DataStoreRepository(
    private val context: Context
) {
    suspend fun putStringSet(key: String, value: Set<String>) {
        val preferencesKey = stringSetPreferencesKey(key)
        context.datastore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    suspend fun getStringSet(key: String): Set<String>? {
        val preferencesKey = stringSetPreferencesKey(key)
        val preferences = context.datastore.data.first()
        return preferences[preferencesKey]
    }
}