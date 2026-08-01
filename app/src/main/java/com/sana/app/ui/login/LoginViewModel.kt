package com.sana.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sana.app.core.repository.UserRepository
import com.sana.app.core.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 🌿 SANA - ViewModel de Login
 * 
 * Maneja el estado y la lógica de autenticación.
 * Soporta tres tipos de login:
 * - Por código de acceso (DOC-XXXXXX, ESC-XXXXXX)
 * - Por usuario y contraseña
 * - Código maestro de administrador
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Intenta login con código de acceso
     */
    fun loginWithCode(code: String, loginType: LoginType) {
        if (code.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Ingresa un código de acceso"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = when (loginType) {
                LoginType.ADMIN -> userRepository.adminLogin(code)
                else -> userRepository.loginByCode(code)
            }

            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        userId = user.id,
                        sessionToken = user.sessionToken,
                        userRole = user.role
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Error al iniciar sesión"
                    )
                }
            )
        }
    }

    /**
     * Intenta login con usuario y contraseña
     */
    fun loginWithCredentials(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Completa todos los campos"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = userRepository.login(username, password)

            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        userId = user.id,
                        sessionToken = user.sessionToken,
                        userRole = user.role
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Credenciales inválidas"
                    )
                }
            )
        }
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Reinicia el estado
     */
    fun resetState() {
        _uiState.value = LoginUiState()
    }
}

/**
 * Estado de la UI de login
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val userId: Long? = null,
    val sessionToken: String? = null,
    val userRole: String? = null
)