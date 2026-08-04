package com.sana.app.ui.games

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sana.app.core.database.entities.GameEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GamesViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(GamesUiState())
    val uiState: StateFlow<GamesUiState> = _uiState.asStateFlow()
}

data class GamesUiState(val games: List<GameEntity> = emptyList(), val isLoading: Boolean = false)
