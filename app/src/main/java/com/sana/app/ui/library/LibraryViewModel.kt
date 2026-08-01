package com.sana.app.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sana.app.core.database.dao.StudyPlanDao
import com.sana.app.core.database.entities.StudyPlanEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 🌿 SANA - ViewModel de Biblioteca
 */
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val studyPlanDao: StudyPlanDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadPublicPlans()
    }

    private fun loadPublicPlans() {
        viewModelScope.launch {
            studyPlanDao.getPublicPlans().collect { plans ->
                _uiState.value = LibraryUiState(plans = plans)
            }
        }
    }
}

data class LibraryUiState(
    val plans: List<StudyPlanEntity> = emptyList(),
    val isLoading: Boolean = false
)