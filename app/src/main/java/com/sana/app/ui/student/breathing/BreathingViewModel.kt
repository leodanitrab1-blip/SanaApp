package com.sana.app.ui.student.breathing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sana.app.core.database.dao.BreathingSessionDao
import com.sana.app.core.database.entities.BreathingSessionEntity
import com.sana.app.core.utils.BreathingExercise
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 🌿 SANA - ViewModel de Ejercicios de Respiración
 * 
 * Controla la lógica de los ejercicios de respiración:
 * - Fases: inhale, hold, exhale, rest
 * - Temporizador por fase
 * - Contador de rondas
 * - Registro de sesiones completadas
 */
@HiltViewModel
class BreathingViewModel @Inject constructor(
    private val breathingSessionDao: BreathingSessionDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(BreathingUiState())
    val uiState: StateFlow<BreathingUiState> = _uiState.asStateFlow()

    private var exerciseJob: Job? = null
    private var phaseJob: Job? = null

    /**
     * Selecciona un ejercicio para comenzar
     */
    fun selectExercise(exercise: BreathingExercise) {
        stopExercise()
        _uiState.value = _uiState.value.copy(
            selectedExercise = exercise,
            totalRounds = exercise.rounds,
            currentRound = 1,
            currentPhase = "ready",
            secondsLeft = 3,
            isPlaying = false
        )
    }

    /**
     * Inicia o reanuda el ejercicio
     */
    fun startExercise() {
        val exercise = _uiState.value.selectedExercise ?: return
        
        _uiState.value = _uiState.value.copy(isPlaying = true)
        
        exerciseJob = viewModelScope.launch {
            val totalRounds = exercise.rounds
            
            for (round in _uiState.value.currentRound..totalRounds) {
                _uiState.value = _uiState.value.copy(currentRound = round)
                
                // Fase: Inhala
                if (!_uiState.value.isPlaying) break
                runPhase("inhale", exercise.inhaleSeconds)
                
                // Fase: Retén
                if (!_uiState.value.isPlaying) break
                if (exercise.holdSeconds > 0) {
                    runPhase("hold", exercise.holdSeconds)
                }
                
                // Fase: Exhala
                if (!_uiState.value.isPlaying) break
                runPhase("exhale", exercise.exhaleSeconds)
                
                // Pequeña pausa entre rondas
                if (round < totalRounds) {
                    runPhase("rest", 1)
                }
            }
            
            // Ejercicio completado
            if (_uiState.value.currentRound >= totalRounds) {
                _uiState.value = _uiState.value.copy(
                    currentPhase = "completed",
                    isPlaying = false
                )
                
                // Registrar sesión
                saveSession(exercise)
            }
        }
    }

    /**
     * Ejecuta una fase del ejercicio (inhale, hold, exhale, rest)
     */
    private suspend fun runPhase(phase: String, durationSeconds: Int) {
        _uiState.value = _uiState.value.copy(
            currentPhase = phase,
            secondsLeft = durationSeconds
        )
        
        for (second in durationSeconds downTo 1) {
            delay(1000L)
            _uiState.value = _uiState.value.copy(secondsLeft = second - 1)
        }
    }

    /**
     * Pausa el ejercicio
     */
    fun pauseExercise() {
        _uiState.value = _uiState.value.copy(isPlaying = false)
        exerciseJob?.cancel()
        phaseJob?.cancel()
    }

    /**
     * Detiene el ejercicio completamente
     */
    fun stopExercise() {
        exerciseJob?.cancel()
        phaseJob?.cancel()
        _uiState.value = BreathingUiState()
    }

    /**
     * Guarda la sesión completada en la base de datos
     */
    private fun saveSession(exercise: BreathingExercise) {
        viewModelScope.launch {
            try {
                val totalSeconds = (exercise.inhaleSeconds + exercise.holdSeconds + exercise.exhaleSeconds) * exercise.rounds
                val session = BreathingSessionEntity(
                    userId = 0, // Se obtendría del usuario logueado
                    exerciseId = exercise.id,
                    exerciseName = exercise.name,
                    durationSeconds = totalSeconds,
                    roundsCompleted = exercise.rounds,
                    isCompleted = true
                )
                breathingSessionDao.insertSession(session)
            } catch (e: Exception) {
                // Error no crítico, solo loguear
            }
        }
    }
}

/**
 * Estado de la UI de respiración
 */
data class BreathingUiState(
    val selectedExercise: BreathingExercise? = null,
    val currentPhase: String = "ready",
    val secondsLeft: Int = 3,
    val currentRound: Int = 1,
    val totalRounds: Int = 0,
    val isPlaying: Boolean = false
)