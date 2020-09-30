package com.knowtech.kt_notes.util

import android.content.Context
import android.util.Log
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import androidx.datastore.preferences.Preferences
import com.knowtech.kt_notes.util.Constants.Companion.PREFERENCE_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class Preferences(context: Context) {

    // preferences key
    private object PreferenceKeys {
        val name = preferencesKey<Int>("Mode")
    }

    // Initializing data store
    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCE_NAME
    )

    suspend fun saveToDataStore(mode: Int) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.name] = mode
        }
    }

    val getMode: Flow<Int> = dataStore.data
        .catch { exception ->
            if(exception is IOException) {
                Log.d("Datastore : ", exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val myMode = preferences[PreferenceKeys.name] ?: 0
            myMode
        }

}