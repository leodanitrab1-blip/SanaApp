package com.sana.app.core.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sana_theme_settings")

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("app_theme")
        const val THEME_DARK = "dark"
        const val THEME_LIGHT = "light"
        const val THEME_SYSTEM = "system"
    }

    val currentTheme: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: THEME_DARK
    }

    val fontScale: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[floatPreferencesKey("font_scale")] ?: 1.0f
    }

    val isDynamicColorEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey("dynamic_colors")] ?: false
    }

    val areAnimationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey("animations_enabled")] ?: true
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }

    suspend fun toggleTheme() {
        context.dataStore.edit { preferences ->
            val current = preferences[THEME_KEY] ?: THEME_DARK
            preferences[THEME_KEY] = if (current == THEME_DARK) THEME_LIGHT else THEME_DARK
        }
    }
}
