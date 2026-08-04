package com.sana.app.ui.student.diary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sana.app.core.database.entities.DiaryEntryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState: StateFlow<DiaryUiState> = _uiState.asStateFlow()
}

data class DiaryUiState(val entries: List<DiaryEntryEntity> = emptyList(), val predominantMood: String = "", val weeklyCount: Int = 0)
