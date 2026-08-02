package com.sana.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sana.app.core.repository.DataRepository
import com.sana.app.core.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dataRepository.initialize()
        }
    }

    fun tryLogin(code: String, loginType: LoginType) {
        if (code.isBlank()) {
            _uiState.value = LoginUiState(error = "Ingresa el código")
            return
        }

        val cleanCode = code.uppercase().trim()
        _uiState.value = LoginUiState(isLoading = true)

        viewModelScope.launch {
            val user = dataRepository.findUserByCode(cleanCode)
            
            if (user != null) {
                // Verificar que el rol coincide con el tipo de login
                val validRole = when (loginType) {
                    LoginType.ADMIN -> user.role == Constants.ROLE_ADMIN
                    LoginType.SCHOOL -> user.role in listOf(Constants.ROLE_TEACHER, Constants.ROLE_DIRECTOR, Constants.ROLE_STUDENT)
                    LoginType.USER -> true
                }
                
                if (validRole) {
                    _uiState.value = LoginUiState(
                        isSuccess = true,
                        userId = user.code.hashCode().toLong(),
                        userRole = user.role,
                        userName = user.name
                    )
                } else {
                    _uiState.value = LoginUiState(
                        error = "Este código no corresponde a este tipo de acceso"
                    )
                }
            } else {
                _uiState.value = LoginUiState(
                    error = "Código no registrado en el sistema"
                )
            }
        }
    }
    
    fun registerStudent(name: String): String {
        val code = dataRepository.generateCode("STU")
        viewModelScope.launch {
            dataRepository.saveUser(
                com.sana.app.core.repository.UserRecord(
                    code = code,
                    role = Constants.ROLE_STUDENT,
                    name = name,
                    active = true,
                    createdAt = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())
                )
            )
        }
        return code
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val userId: Long? = null,
    val userRole: String? = null,
    val userName: String? = null
)
