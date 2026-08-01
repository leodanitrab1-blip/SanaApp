package com.sana.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sana.app.core.database.dao.UserDao
import com.sana.app.core.database.entities.UserEntity
import com.sana.app.core.utils.Constants
import com.sana.app.core.utils.sha256
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun loginWithCode(code: String, loginType: LoginType) {
        if (code.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Ingresa un código de acceso")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Verificar si es el código maestro de admin
                if (loginType == LoginType.ADMIN && code == Constants.ADMIN_MASTER_CODE) {
                    // Buscar o crear usuario admin
                    var adminUser = userDao.getUserByCode(code)
                    if (adminUser == null) {
                        // Crear admin por primera vez
                        adminUser = UserEntity(
                            username = "admin",
                            passwordHash = "admin123".sha256(),
                            fullName = "Administrador Sana",
                            role = Constants.ROLE_ADMIN,
                            accessCode = code
                        )
                        val id = userDao.insertUser(adminUser)
                        adminUser = adminUser.copy(id = id)
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        userId = adminUser.id,
                        sessionToken = "admin_session",
                        userRole = Constants.ROLE_ADMIN
                    )
                    return@launch
                }

                // Login normal por código
                val user = userDao.getUserByCode(code)
                if (user != null && user.isActive) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        userId = user.id,
                        sessionToken = "session_${user.id}",
                        userRole = user.role
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Código no encontrado o inactivo"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    fun loginWithCredentials(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Completa todos los campos")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val passwordHash = password.sha256()
                val user = userDao.login(username, passwordHash)

                if (user != null && user.isActive) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        userId = user.id,
                        sessionToken = "session_${user.id}",
                        userRole = user.role
                    )
                } else {
                    // Para el admin, permitir login simple
                    if (username == "admin" && password == "admin123") {
                        var adminUser = userDao.getUserByCode(Constants.ADMIN_MASTER_CODE)
                        if (adminUser == null) {
                            adminUser = UserEntity(
                                username = "admin",
                                passwordHash = "admin123".sha256(),
                                fullName = "Administrador Sana",
                                role = Constants.ROLE_ADMIN,
                                accessCode = Constants.ADMIN_MASTER_CODE
                            )
                            val id = userDao.insertUser(adminUser)
                            adminUser = adminUser.copy(id = id)
                        }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            userId = adminUser.id,
                            sessionToken = "admin_session",
                            userRole = Constants.ROLE_ADMIN
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Usuario o contraseña incorrectos"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val userId: Long? = null,
    val sessionToken: String? = null,
    val userRole: String? = null
)
