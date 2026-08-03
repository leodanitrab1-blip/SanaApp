package com.sana.app.ui.login

import androidx.lifecycle.ViewModel
import com.sana.app.core.repository.DataRepository
import com.sana.app.core.repository.UserRecord
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

    init { try { dataRepository.initialize() } catch (_: Exception) { } }

    fun tryLogin(code: String, loginType: LoginType) {
        if (code.isBlank()) { _uiState.value = LoginUiState(error = "Ingresa el código"); return }
        
        val cleanCode = code.uppercase().trim()
        val user = dataRepository.findUserByCode(cleanCode)
        
        if (user != null && user.active) {
            // ACEPTAR cualquier rol para SCHOOL (docente, director, alumno)
            _uiState.value = LoginUiState(
                isSuccess = true, 
                userId = cleanCode.hashCode().toLong(), 
                userRole = user.role, 
                userName = user.name
            )
        } else {
            // Buscar también en escuelas (código ADM guardado como director)
            val schools = dataRepository.getAllSchools()
            val school = schools.find { it.adminCode == cleanCode }
            if (school != null) {
                // Es un director registrado desde admin
                _uiState.value = LoginUiState(
                    isSuccess = true,
                    userId = cleanCode.hashCode().toLong(),
                    userRole = Constants.ROLE_DIRECTOR,
                    userName = school.directorName
                )
            } else {
                _uiState.value = LoginUiState(error = "Código no registrado")
            }
        }
    }
    
    fun registerStudent(name: String): String {
        return try {
            val code = dataRepository.generateCode("STU")
            dataRepository.saveUser(UserRecord(code = code, role = Constants.ROLE_STUDENT, name = name))
            code
        } catch (e: Exception) { "ERROR" }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
}

data class LoginUiState(
    val isSuccess: Boolean = false,
    val error: String? = null,
    val userId: Long? = null,
    val userRole: String? = null,
    val userName: String? = null
)
