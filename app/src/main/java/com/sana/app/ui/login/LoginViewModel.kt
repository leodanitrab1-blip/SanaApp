package com.sana.app.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sana.app.core.repository.FirebaseRepository
import com.sana.app.core.repository.UserRecord
import com.sana.app.core.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = FirebaseRepository(application)
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repo.initialize()
            // 🔥 SINCRONIZAR DATOS DE FIREBASE AL ABRIR LA APP
            val synced = repo.syncAll()
            android.util.Log.d("SANA", "Sincronizados $synced registros de Firebase")
        }
    }

    fun tryLogin(code: String, loginType: LoginType) {
        if (code.isBlank()) { _uiState.value = LoginUiState(error = "Ingresa el código"); return }
        val c = code.uppercase().trim()
        val user = repo.findUserByCode(c)
        if (user != null && user.active) {
            _uiState.value = LoginUiState(isSuccess = true, userId = c.hashCode().toLong(), userRole = user.role, userName = user.name)
        } else {
            val schools = repo.getAllSchools()
            val school = schools.find { it.adminCode == c }
            if (school != null) _uiState.value = LoginUiState(isSuccess = true, userId = c.hashCode().toLong(), userRole = Constants.ROLE_DIRECTOR, userName = school.directorName)
            else if (schools.any { s -> s.teacherCodes.contains(c) }) _uiState.value = LoginUiState(isSuccess = true, userId = c.hashCode().toLong(), userRole = Constants.ROLE_TEACHER, userName = "Docente")
            else _uiState.value = LoginUiState(error = "Código no registrado")
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
