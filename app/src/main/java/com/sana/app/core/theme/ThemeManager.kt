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

// Extensión para crear DataStore a nivel aplicación
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "sana_theme_settings"
)

/**
 * 🌿 SANA - Gestor de Temas
 * 
 * Maneja la persistencia y cambio de temas de la aplicación.
 * Soporta:
 * - Tema Oscuro: Fondo degradado oscuro con estrellas
 * - Tema Claro: Fondo blanco hueso con motivos naturales
 * 
 * La preferencia se guarda en DataStore para persistencia eficiente.
 * 
 * @param context Contexto de la aplicación (inyectado por Hilt)
 */
@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        // Claves para DataStore
        private val THEME_KEY = stringPreferencesKey("app_theme")
        private val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_colors")
        private val ANIMATIONS_ENABLED_KEY = booleanPreferencesKey("animations_enabled")
        private val STAR_COUNT_KEY = intPreferencesKey("star_count")
        private val FONT_SCALE_KEY = floatPreferencesKey("font_scale")

        // Constantes de tema
        const val THEME_DARK = "dark"
        const val THEME_LIGHT = "light"
        const val THEME_SYSTEM = "system"

        // Configuraciones por defecto
        const val DEFAULT_STAR_COUNT = 150
        const val DEFAULT_FONT_SCALE = 1.0f
    }

    /**
     * Flujo reactivo del tema actual
     * Emite: "dark", "light", o "system"
     */
    val currentTheme: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: THEME_DARK // Oscuro por defecto (más relajante)
    }

    /**
     * Flujo para colores dinámicos (Android 12+)
     */
    val isDynamicColorEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DYNAMIC_COLOR_KEY] ?: false
    }

    /**
     * Flujo para animaciones habilitadas
     */
    val areAnimationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ANIMATIONS_ENABLED_KEY] ?: true
    }

    /**
     * Flujo para cantidad de estrellas en fondo oscuro
     */
    val starCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[STAR_COUNT_KEY] ?: DEFAULT_STAR_COUNT
    }

    /**
     * Flujo para escala de fuente
     */
    val fontScale: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[FONT_SCALE_KEY] ?: DEFAULT_FONT_SCALE
    }

    // ============================================
    // OPERACIONES DE TEMA
    // ============================================

    /**
     * Establecer tema específico
     * 
     * @param theme THEME_DARK, THEME_LIGHT, o THEME_SYSTEM
     */
    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }

    /**
     * Alternar entre tema oscuro y claro
     */
    suspend fun toggleTheme() {
        context.dataStore.edit { preferences ->
            val current = preferences[THEME_KEY] ?: THEME_DARK
            preferences[THEME_KEY] = when (current) {
                THEME_DARK -> THEME_LIGHT
                THEME_LIGHT -> THEME_DARK
                else -> THEME_DARK
            }
        }
    }

    /**
     * Establecer si el tema actual es oscuro (útil para UI)
     */
    suspend fun isDarkTheme(): Boolean {
        return context.dataStore.data.map { preferences ->
            val theme = preferences[THEME_KEY] ?: THEME_DARK
            when (theme) {
                THEME_DARK -> true
                THEME_LIGHT -> false
                else -> true // System: determinar por configuración del dispositivo
            }
        }.let { flow ->
            var isDark = true
            flow.collect { isDark = it }
            isDark
        }
    }

    // ============================================
    // CONFIGURACIONES ADICIONALES
    // ============================================

    /**
     * Habilitar/deshabilitar colores dinámicos (Material You)
     */
    suspend fun setDynamicColors(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_KEY] = enabled
        }
    }

    /**
     * Habilitar/deshabilitar animaciones
     */
    suspend fun setAnimationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ANIMATIONS_ENABLED_KEY] = enabled
        }
    }

    /**
     * Establecer cantidad de estrellas en el fondo oscuro
     */
    suspend fun setStarCount(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[STAR_COUNT_KEY] = count.coerceIn(50, 300)
        }
    }

    /**
     * Establecer escala de fuente
     */
    suspend fun setFontScale(scale: Float) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SCALE_KEY] = scale.coerceIn(0.8f, 1.5f)
        }
    }

    /**
     * Restablecer todas las configuraciones de tema a valores por defecto
     */
    suspend fun resetToDefaults() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}