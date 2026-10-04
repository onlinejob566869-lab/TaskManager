package com.taskmanager.app.ui.screens.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.taskmanager.app.data.model.DarkModePref
import com.taskmanager.app.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = Constants.DATASTORE_PREFS)

class SettingsViewModel(private val context: Context) {

    private val darkModeKey = stringPreferencesKey(Constants.DARK_MODE_KEY)

    val darkModeFlow: Flow<DarkModePref> = context.dataStore.data
        .map { prefs ->
            DarkModePref.fromName(prefs[darkModeKey])
        }

    suspend fun setDarkMode(pref: DarkModePref) {
        context.dataStore.edit { it[darkModeKey] = pref.name }
    }
}
