package com.sana.app.ui.student.diary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sana.app.core.database.entities.EmergencyContactEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EmergencyViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(EmergencyUiState())
    val uiState: StateFlow<EmergencyUiState> = _uiState.asStateFlow()
    init { _uiState.value = EmergencyUiState(contacts = listOf(
        EmergencyContactEntity(1, "Línea de la Vida", "800-911-2000", "Atención en crisis 24/7", "México", "SUICIDE", null, true, "24_7", true),
        EmergencyContactEntity(2, "SAPTEL", "55-5259-8121", "Apoyo psicológico", "México", "ANXIETY", null, true, "24_7", true),
        EmergencyContactEntity(3, "Línea 135", "135", "Atención al suicida", "Argentina", "SUICIDE", null, true, "24_7", true),
        EmergencyContactEntity(4, "Línea 106", "106", "Salud mental", "Colombia", "GENERAL", null, true, "24_7", true),
        EmergencyContactEntity(5, "Teléfono Esperanza", "717-003-717", "Apoyo emocional", "España", "GENERAL", null, true, "24_7", true),
        EmergencyContactEntity(6, "Línea 024", "024", "Conducta suicida", "España", "SUICIDE", null, true, "24_7", true),
        EmergencyContactEntity(7, "Salud Responde", "600-360-7777", "Orientación", "Chile", "GENERAL", null, true, "24_7", true),
        EmergencyContactEntity(8, "Línea 113", "113", "Salud mental", "Perú", "GENERAL", null, true, "24_7", true),
        EmergencyContactEntity(9, "Crisis Text Line", "741741", "Envía HOME al 741741", "Internacional", "SUICIDE", null, true, "24_7", true)
    )) }
}

data class EmergencyUiState(val contacts: List<EmergencyContactEntity> = emptyList(), val isLoading: Boolean = false)
