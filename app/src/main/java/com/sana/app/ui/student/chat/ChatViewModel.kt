package com.sana.app.ui.student.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(userId: Long, content: String) {
        val ts = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        _uiState.value = _uiState.value.copy(messages = _uiState.value.messages + ChatMessage("user", content, ts), isLoading = true)
        viewModelScope.launch { delay(800); val r = generateResponse(content); _uiState.value = _uiState.value.copy(messages = _uiState.value.messages + ChatMessage("assistant", r, SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())), isLoading = false) }
    }

    private fun generateResponse(msg: String): String {
        val m = msg.lowercase().trim()
        return when {
            m.contains("hola") || m.contains("hey") -> "¡Hola! 🌟 ¿Cómo te sientes hoy?"
            m.contains("triste") || m.contains("deprimido") -> "Siento que te sientas así. 🤍 No estás solo/a. ¿Quieres contarme más?"
            m.contains("ansioso") || m.contains("ansiedad") -> "Respira conmigo: inhala 4s, retén 4s, exhala 6s. 🌿 ¿Mejor?"
            m.contains("feliz") || m.contains("bien") -> "¡Qué bueno! 😊 ¿Qué te hizo sentir bien hoy?"
            m.contains("enojado") || m.contains("frustrado") -> "Es válido sentirse así. 💪 ¿Qué pasó?"
            m.contains("ayuda") || m.contains("emergencia") -> "🆘 Línea de la Vida: 800-911-2000. No estás solo/a. 💚"
            m.contains("adiós") || m.contains("bye") -> "¡Cuídate mucho! 🌟 Vuelve cuando me necesites."
            else -> "Te escucho. 💭 Cuéntame más, estoy aquí para ti."
        }
    }
    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
}

data class ChatMessage(val role: String, val content: String, val timestamp: String)
data class ChatUiState(val messages: List<ChatMessage> = emptyList(), val isLoading: Boolean = false, val error: String? = null)
