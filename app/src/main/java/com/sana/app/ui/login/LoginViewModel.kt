package com.sana.app.ui.login

import androidx.lifecycle.ViewModel
import com.sana.app.core.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun tryLogin(code: String, loginType: LoginType) {
        if (code.isBlank()) {
            _uiState.value = LoginUiState(error = "Ingresa el código")
            return
        }

        // ADMIN: código maestro
        if (loginType == LoginType.ADMIN) {
            if (code.uppercase().trim() == Constants.ADMIN_MASTER_CODE) {
                _uiState.value = LoginUiState(isSuccess = true, userId = 0L)
            } else {
                _uiState.value = LoginUiState(error = "Código de administrador incorrecto")
            }
            return
        }

        // SCHOOL/USER: cualquier código de 10 caracteres
        if (code.length >= 10) {
            _uiState.value = LoginUiState(isSuccess = true, userId = 1L)
        } else {
            _uiState.value = LoginUiState(error = "Código inválido. Debe tener al menos 10 caracteres")
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class LoginUiState(
    val isSuccess: Boolean = false,
    val error: String? = null,
    val userId: Long? = null
)
