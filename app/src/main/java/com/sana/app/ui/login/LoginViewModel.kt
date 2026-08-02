package com.sana.app.ui.login

import androidx.lifecycle.ViewModel
import com.sana.app.core.repository.DataRepository
import com.sana.app.core.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Inicializar base de datos local
        try {
            dataRepository.initialize()
        } catch (e: Exception) {
            // Si falla, continuamos igual
        }
    }

    fun tryLogin(code: String, loginType: LoginType) {
        if (code.isBlank()) {
            _uiState.value = LoginUiState(error = "Ingresa el código")
            return
        }

        val cleanCode = code.uppercase().trim()

        try {
            val user = dataRepository.findUserByCode(cleanCode)
            
            if (user != null) {
                _uiState.value = LoginUiState(
                    isSuccess = true,
                    userId = cleanCode.hashCode().toLong(),
                    userRole = user.role,
                    userName = user.name
                )
            } else {
                _uiState.value = LoginUiState(
                    error = "Código no registrado"
                )
            }
        } catch (e: Exception) {
            _uiState.value = LoginUiState(
                error = "Error al verificar. Intenta de nuevo."
            )
        }
    }
    
    fun registerStudent(name: String): String {
        return try {
            val code = dataRepository.generateCode("STU")
            dataRepository.saveUser(
                UserRecord(
                    code = code,
                    role = Constants.ROLE_STUDENT,
                    name = name,
                    active = true,
                    createdAt = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())
                )
            )
            code
        } catch (e: Exception) {
            "ERROR-XXXXXX"
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class LoginUiState(
    val isSuccess: Boolean = false,
    val error: String? = null,
    val userId: Long? = null,
    val userRole: String? = null,
    val userName: String? = null
)
