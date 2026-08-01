package com.sana.app.ui.student.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sana.app.core.database.dao.EmergencyContactDao
import com.sana.app.core.database.entities.EmergencyContactEntity
import com.sana.app.core.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 🌿 SANA - ViewModel de Líneas de Emergencia
 * 
 * Carga contactos desde BD local y sincroniza con remoto.
 */
@HiltViewModel
class EmergencyViewModel @Inject constructor(
    private val emergencyContactDao: EmergencyContactDao,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmergencyUiState())
    val uiState: StateFlow<EmergencyUiState> = _uiState.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            // Cargar desde BD local primero
            emergencyContactDao.getAllActiveContacts().collect { contacts ->
                if (contacts.isNotEmpty()) {
                    _uiState.value = EmergencyUiState(contacts = contacts)
                }
            }
        }
    }
}

data class EmergencyUiState(
    val contacts: List<EmergencyContactEntity> = emptyList(),
    val isLoading: Boolean = false
)