package com.sana.app.ui.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sana.app.core.database.entities.StudyPlanEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()
}

data class LibraryUiState(val plans: List<StudyPlanEntity> = emptyList(), val isLoading: Boolean = false)
