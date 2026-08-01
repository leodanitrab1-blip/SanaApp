package com.sana.app.ui.student.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sana.app.core.database.dao.DiaryDao
import com.sana.app.core.database.entities.DiaryEntryEntity
import com.sana.app.core.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 🌿 SANA - ViewModel del Diario Emocional
 */
@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val diaryDao: DiaryDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState: StateFlow<DiaryUiState> = _uiState.asStateFlow()

    init {
        loadEntries(0) // userId se obtendría del usuario logueado
    }

    fun loadEntries(userId: Long) {
        viewModelScope.launch {
            diaryRepository.getEntriesForUser(userId).collect { entries ->
                // Obtener resumen semanal
                val summary = diaryRepository.getWeeklyMoodSummary(userId)
                _uiState.value = DiaryUiState(
                    entries = entries,
                    predominantMood = summary.predominantMood,
                    weeklyCount = summary.totalEntries
                )
            }
        }
    }

    fun saveEntry(userId: Long, mood: String, content: String, title: String?) {
        viewModelScope.launch {
            diaryRepository.saveEntry(
                userId = userId,
                mood = mood,
                content = content,
                title = title
            )
        }
    }

    fun deleteEntry(entryId: Long) {
        viewModelScope.launch {
            diaryRepository.deleteEntry(entryId)
        }
    }
}

data class DiaryUiState(
    val entries: List<DiaryEntryEntity> = emptyList(),
    val predominantMood: String = "",
    val weeklyCount: Int = 0
)