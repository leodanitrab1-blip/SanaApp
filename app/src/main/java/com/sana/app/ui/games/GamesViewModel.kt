package com.sana.app.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sana.app.core.database.entities.GameEntity
import com.sana.app.core.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 🌿 SANA - ViewModel de Juegos
 */
@HiltViewModel
class GamesViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GamesUiState())
    val uiState: StateFlow<GamesUiState> = _uiState.asStateFlow()

    init {
        loadGames()
    }

    private fun loadGames() {
        viewModelScope.launch {
            gameRepository.getAllActiveGames().collect { games ->
                _uiState.value = GamesUiState(games = games)
            }
        }
    }

    fun playGame(gameId: Long) {
        viewModelScope.launch {
            gameRepository.playGame(gameId)
        }
    }
}

data class GamesUiState(
    val games: List<GameEntity> = emptyList(),
    val isLoading: Boolean = false
)