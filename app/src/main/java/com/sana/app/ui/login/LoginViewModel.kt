package com.sana.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sana.app.core.repository.FirebaseRepository
import com.sana.app.core.repository.UserRecord
import com.sana.app.core.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: FirebaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repo.initialize()
            repo.syncFromFirebase() // 🔥 SINCRONIZAR AL ABRIR
        }
    }

    fun tryLogin(code: String, loginType: LoginType) {
        if (code.isBlank()) { _uiState.value = LoginUiState(error = "Ingresa el código"); return }
        val cleanCode = code.uppercase().trim()
        val user = repo.findUserByCode(cleanCode)
        if (user != null && user.active) {
            _uiState.value = LoginUiState(isSuccess = true, userId = cleanCode.hashCode().toLong(), userRole = user.role, userName = user.name)
        } else {
            // Buscar en escuelas
            val schools = repo.getAllLocalSchools()
            val school = schools.find { it.adminCode == cleanCode || it.code == cleanCode }
            if (school != null) {
                _uiState.value = LoginUiState(isSuccess = true, userId = cleanCode.hashCode().toLong(), userRole = Constants.ROLE_DIRECTOR, userName = school.directorName)
            } else if (schools.any { it.teacherCodes.contains(cleanCode) }) {
                _uiState.value = LoginUiState(isSuccess = true, userId = cleanCode.hashCode().toLong(), userRole = Constants.ROLE_TEACHER, userName = "Docente")
            } else {
                _uiState.value = LoginUiState(error = "Código no registrado")
            }
        }
    }
    
    fun registerStudent(name: String): String {
        val code = repo.generateCode("STU")
        repo.saveUser(UserRecord(code = code, role = Constants.ROLE_STUDENT, name = name))
        return code
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
}

data class LoginUiState(val isSuccess: Boolean = false, val error: String? = null, val userId: Long? = null, val userRole: String? = null, val userName: String? = null)
