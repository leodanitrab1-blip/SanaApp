package com.sana.app.ui.student.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sana.app.BuildConfig
import com.sana.app.core.network.GroqApiService
import com.sana.app.core.network.GroqChatRequest
import com.sana.app.core.network.GroqMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * 🌿 SANA - ViewModel del Chat IA
 * 
 * Maneja la lógica del chat con Groq API:
 * - Envío de mensajes
 * - Historial de conversación
 * - Respuestas offline cuando no hay conexión
 * - Sistema de prompts para asistencia emocional
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val groqApiService: GroqApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val conversationHistory = mutableListOf<GroqMessage>()

    init {
        // Mensaje inicial del sistema
        conversationHistory.add(
            GroqMessage(
                role = "system",
                content = """Eres Sana, un asistente emocional empático y comprensivo. 
                Tu objetivo es brindar apoyo emocional a jóvenes y adolescentes.
                Reglas:
                - Sé amable, empático y sin prejuicios
                - No des consejos médicos profesionales
                - Si detectas riesgo de autolesión o suicidio, recomienda buscar ayuda profesional y proporciona líneas de emergencia
                - Usa un lenguaje cercano pero respetuoso
                - Mantén la confidencialidad
                - Responde en español"""
            )
        )
    }

    /**
     * Envía un mensaje del usuario y obtiene respuesta de la IA
     */
    fun sendMessage(userId: Long, content: String) {
        val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val userMessage = ChatMessage(role = "user", content = content, timestamp = timestamp)

        // Agregar mensaje del usuario al estado
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            isLoading = true,
            error = null
        )

        // Agregar al historial de conversación
        conversationHistory.add(GroqMessage(role = "user", content = content))

        viewModelScope.launch {
            try {
                val request = GroqChatRequest(
                    model = "mixtral-8x7b-32768",
                    messages = conversationHistory.toList(),
                    temperature = 0.7,
                    max_tokens = 1024
                )

                val response = groqApiService.getChatCompletion(
                    auth = "Bearer ${BuildConfig.GROQ_API_KEY}",
                    request = request
                )

                if (response.isSuccessful) {
                    val assistantMessage = response.body()?.choices?.firstOrNull()?.message
                    if (assistantMessage != null) {
                        conversationHistory.add(assistantMessage)

                        val chatMessage = ChatMessage(
                            role = "assistant",
                            content = assistantMessage.content,
                            timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                        )

                        _uiState.value = _uiState.value.copy(
                            messages = _uiState.value.messages + chatMessage,
                            isLoading = false
                        )
                    } else {
                        throw Exception("Respuesta vacía del asistente")
                    }
                } else {
                    // Si falla la API, usar respuesta offline
                    val offlineResponse = getOfflineResponse(content)
                    val chatMessage = ChatMessage(
                        role = "assistant",
                        content = offlineResponse,
                        timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + chatMessage,
                        isLoading = false,
                        error = "Usando modo offline - sin conexión a Internet"
                    )
                }
            } catch (e: Exception) {
                // Error de red: usar respuesta offline
                val offlineResponse = getOfflineResponse(content)
                val chatMessage = ChatMessage(
                    role = "assistant",
                    content = offlineResponse,
                    timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                )
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + chatMessage,
                    isLoading = false,
                    error = "Modo offline - conecta a Internet para respuestas IA completas"
                )
            }
        }
    }

    /**
     * Respuestas offline pregrabadas para cuando no hay conexión
     */
    private fun getOfflineResponse(userMessage: String): String {
        val message = userMessage.lowercase()
        return when {
            message.contains("triste") || message.contains("deprimido") ->
                "Entiendo que te sientas así. Es normal tener días difíciles. " +
                "¿Quieres hablarme más sobre lo que te preocupa? A veces compartir " +
                "nuestros sentimientos ayuda a sentirnos mejor. 🤍"

            message.contains("ansioso") || message.contains("ansiedad") ->
                "La ansiedad puede ser abrumadora. Vamos a intentar un ejercicio juntos: " +
                "inhala profundamente por 4 segundos, mantén el aire 4 segundos, " +
                "y exhala lentamente por 6 segundos. Repite 3 veces. ¿Mejor? 🌿"

            message.contains("feliz") || message.contains("bien") ->
                "¡Qué bueno escuchar eso! 😊 Es importante celebrar los momentos felices. " +
                "¿Qué fue lo que te hizo sentir bien hoy?"

            message.contains("ayuda") || message.contains("emergencia") ->
                "Si estás en una situación de emergencia, por favor contacta a las líneas de ayuda:\n" +
                "📞 Línea de la Vida (México): 800-911-2000\n" +
                "📞 SAPTEL: 55-5259-8121\n" +
                "No estás solo/a. Hay personas que quieren ayudarte. 💚"

            message.contains("hola") || message.contains("buenos") ->
                "¡Hola! 🌟 Bienvenido/a a tu espacio seguro. " +
                "Cuéntame cómo te sientes hoy, estoy aquí para escucharte."

            else ->
                "Gracias por compartir conmigo. Estoy aquí para escucharte sin juzgarte. " +
                "¿Hay algo específico en lo que pueda ayudarte hoy? 💭"
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ChatMessage(
    val role: String, // "user" o "assistant"
    val content: String,
    val timestamp: String
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)