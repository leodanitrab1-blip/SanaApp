package com.sana.app.ui.student.diary

import androidx.lifecycle.ViewModel
import com.sana.app.core.database.entities.EmergencyContactEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EmergencyViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(EmergencyUiState())
    val uiState: StateFlow<EmergencyUiState> = _uiState.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        // Contactos precargados
        val contacts = listOf(
            EmergencyContactEntity(1, "Línea de la Vida", "800-911-2000", "Atención en crisis 24/7", "México", "SUICIDE", null, true, "24_7", true),
            EmergencyContactEntity(2, "SAPTEL", "55-5259-8121", "Apoyo psicológico gratuito", "México", "ANXIETY", null, true, "24_7", true),
            EmergencyContactEntity(3, "Línea Diversa", "55-5658-1111", "Apoyo comunidad LGBTQ+", "México", "GENERAL", null, true, "24_7", true),
            EmergencyContactEntity(4, "Línea 135", "135", "Atención al suicida", "Argentina", "SUICIDE", null, true, "24_7", true),
            EmergencyContactEntity(5, "Línea 106", "106", "Salud mental", "Colombia", "GENERAL", null, true, "24_7", true),
            EmergencyContactEntity(6, "Teléfono Esperanza", "717-003-717", "Apoyo emocional", "España", "GENERAL", null, true, "24_7", true),
            EmergencyContactEntity(7, "Línea 024", "024", "Conducta suicida", "España", "SUICIDE", null, true, "24_7", true),
            EmergencyContactEntity(8, "Salud Responde", "600-360-7777", "Orientación en salud", "Chile", "GENERAL", null, true, "24_7", true),
            EmergencyContactEntity(9, "Línea 113", "113", "Salud mental", "Perú", "GENERAL", null, true, "24_7", true),
            EmergencyContactEntity(10, "Prevención Suicidio", "0800-0767", "24 horas", "Uruguay", "SUICIDE", null, true, "24_7", true),
            EmergencyContactEntity(11, "Crisis Text Line", "741741", "Envía HOME al 741741", "Internacional", "SUICIDE", "https://www.crisistextline.org", true, "24_7", true),
            EmergencyContactEntity(12, "Befrienders", "", "Red mundial de ayuda", "Internacional", "GENERAL", "https://www.befrienders.org", true, "24_7", true),
        )
        _uiState.value = EmergencyUiState(contacts = contacts)
    }
}

data class EmergencyUiState(
    val contacts: List<EmergencyContactEntity> = emptyList(),
    val isLoading: Boolean = false
)
