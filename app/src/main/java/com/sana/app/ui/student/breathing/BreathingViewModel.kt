package com.sana.app.ui.student.breathing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sana.app.core.utils.BreathingExercise
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BreathingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BreathingUiState())
    val uiState: StateFlow<BreathingUiState> = _uiState.asStateFlow()
    private var exerciseJob: Job? = null

    fun selectExercise(exercise: BreathingExercise) { stopExercise(); _uiState.value = BreathingUiState(selectedExercise = exercise, totalRounds = exercise.rounds, currentRound = 1, currentPhase = "ready", secondsLeft = 3) }
    
    fun startExercise() {
        val e = _uiState.value.selectedExercise ?: return
        _uiState.value = _uiState.value.copy(isPlaying = true)
        exerciseJob = viewModelScope.launch {
            for (r in _uiState.value.currentRound..e.rounds) {
                _uiState.value = _uiState.value.copy(currentRound = r)
                runPhase("inhale", e.inhaleSeconds)
                if (e.holdSeconds > 0) runPhase("hold", e.holdSeconds)
                runPhase("exhale", e.exhaleSeconds)
                if (r < e.rounds) runPhase("rest", 1)
            }
            _uiState.value = _uiState.value.copy(currentPhase = "completed", isPlaying = false)
        }
    }
    
    private suspend fun runPhase(phase: String, d: Int) {
        _uiState.value = _uiState.value.copy(currentPhase = phase, secondsLeft = d)
        for (s in d downTo 1) { delay(1000); _uiState.value = _uiState.value.copy(secondsLeft = s - 1) }
    }
    
    fun pauseExercise() { _uiState.value = _uiState.value.copy(isPlaying = false); exerciseJob?.cancel() }
    fun stopExercise() { exerciseJob?.cancel(); _uiState.value = BreathingUiState() }
}

data class BreathingUiState(val selectedExercise: BreathingExercise? = null, val currentPhase: String = "ready", val secondsLeft: Int = 3, val currentRound: Int = 1, val totalRounds: Int = 0, val isPlaying: Boolean = false)
