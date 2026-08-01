package com.sana.app.ui.student.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sana.app.core.database.dao.EmergencyContactDao
import com.sana.app.core.database.entities.EmergencyContactEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmergencyViewModel @Inject constructor(
    private val emergencyContactDao: EmergencyContactDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmergencyUiState())
    val uiState: StateFlow<EmergencyUiState> = _uiState.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            emergencyContactDao.getAllActiveContacts().collect { contacts ->
                _uiState.value = EmergencyUiState(contacts = contacts)
            }
        }
    }
}

data class EmergencyUiState(
    val contacts: List<EmergencyContactEntity> = emptyList(),
    val isLoading: Boolean = false
)
