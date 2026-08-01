package com.sana.app.ui.student.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * 🌿 SANA - Chat IA Offline
 * 
 * Asistente emocional que funciona SIN conexión a Internet.
 * No requiere APIs externas ni tokens.
 * 
 * Usa un sistema de patrones y palabras clave para generar
 * respuestas empáticas y útiles.
 */
@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    /**
     * Envía mensaje y genera respuesta offline
     */
    fun sendMessage(userId: Long, content: String) {
        val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val userMessage = ChatMessage(role = "user", content = content, timestamp = timestamp)

        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            isLoading = true
        )

        viewModelScope.launch {
            // Simular tiempo de "pensamiento" (más natural)
            delay(800 + (Math.random() * 1200).toLong())
            
            val response = generateResponse(content)
            val assistantMessage = ChatMessage(
                role = "assistant",
                content = response,
                timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            )

            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + assistantMessage,
                isLoading = false
            )
        }
    }

    /**
     * Genera respuesta basada en el mensaje del usuario
     * Sistema de patrones con múltiples categorías
     */
    private fun generateResponse(message: String): String {
        val msg = message.lowercase().trim()

        // ============================================
        // 1. SALUDOS
        // ============================================
        if (msg.matches(Regex("^(hola|hey|buenos días|buenas tardes|buenas noches|buenas|saludos|hi|hello).*"))) {
            return getRandomResponse(listOf(
                "¡Hola! 🌟 Me alegra verte por aquí. ¿Cómo te sientes hoy?",
                "¡Hey! 💫 Bienvenido/a a tu espacio seguro. Cuéntame, ¿cómo va tu día?",
                "¡Hola, hola! 🌿 Estoy aquí para escucharte. ¿Qué hay en tu corazón hoy?",
                "¡Qué bueno verte! 💚 ¿Cómo te encuentras en este momento?"
            ))
        }

        // ============================================
        // 2. TRISTEZA / DEPRESIÓN
        // ============================================
        if (containsAny(msg, listOf("triste", "deprimido", "deprimida", "llorar", "lloro", "solo", "sola", "soledad", "vacío", "vacia", "sin ganas", "desanimado"))) {
            return getRandomResponse(listOf(
                "Siento mucho que te sientas así. 🤍 No estás solo/a. A veces está bien no estar bien. ¿Quieres contarme más sobre lo que te pasa?",
                "Es completamente válido sentirse triste. 🌧️ Las emociones van y vienen como las nubes. ¿Hay algo específico que te tenga así?",
                "Te abrazo con el corazón. 🫂 La tristeza es parte de ser humano. ¿Has intentado escribir sobre lo que sientes? A veces ayuda sacarlo.",
                "Estoy aquí para ti. 💙 Cuando te sientas listo/a, podemos intentar un ejercicio de respiración juntos. ¿Te gustaría?",
                "Recuerda: después de la tormenta siempre sale el sol. 🌤️ ¿Qué pequeña cosa podrías hacer hoy para cuidarte un poquito?"
            ))
        }

        // ============================================
        // 3. ANSIEDAD / ESTRÉS
        // ============================================
        if (containsAny(msg, listOf("ansioso", "ansiosa", "ansiedad", "estrés", "estresado", "nervioso", "nerviosa", "preocupado", "preocupada", "miedo", "ataque", "pánico", "panico"))) {
            return getRandomResponse(listOf(
                "Vamos a calmarnos juntos. 🌿 Haz una pausa. Inhala profundo por 4 segundos... mantén 4... exhala lento por 6. ¿Lo intentamos?",
                "La ansiedad puede sentirse abrumadora, pero es temporal. 🧘 ¿Puedes identificar qué fue lo que la disparó? A veces nombrarlo ayuda.",
                "Estoy aquí contigo. 🌊 Imagina que tus pensamientos son olas: vienen, pero también se van. Tú eres la playa, firme y tranquila.",
                "Vamos a aterrizar juntos. Mira a tu alrededor y dime: ¿5 cosas que ves? ¿4 que puedes tocar? ¿3 que escuchas? ¿2 que hueles? ¿1 que saboreas?",
                "La ansiedad es un mecanismo de protección, no tu enemiga. 💜 ¿Qué te dirías a ti mismo/a si fueras tu mejor amigo/a ahora?"
            ))
        }

        // ============================================
        // 4. FELICIDAD / ALEGRÍA
        // ============================================
        if (containsAny(msg, listOf("feliz", "contento", "contenta", "alegre", "bien", "genial", "excelente", "fantástico", "maravilloso", "gracias", "mejor"))) {
            return getRandomResponse(listOf(
                "¡Qué alegría leer eso! 😊 Me encanta verte bien. ¿Qué fue lo que te hizo sentir así?",
                "¡Eso es maravilloso! 🌟 Los momentos felices merecen celebrarse. ¿Quieres compartir qué pasó?",
                "¡Me contagias tu felicidad! 🎉 Es importante reconocer y saborear estos momentos. ¿Qué aprendiste de esta experiencia?",
                "¡Brillando! ✨ Así me gusta verte. Recuerda este sentimiento para los días nublados."
            ))
        }

        // ============================================
        // 5. ENOJO / FRUSTRACIÓN
        // ============================================
        if (containsAny(msg, listOf("enojado", "enojada", "furioso", "furiosa", "frustrado", "frustrada", "rabia", "odio", "molesto", "molesta", "harto", "harta"))) {
            return getRandomResponse(listOf(
                "Entiendo tu frustración. 😤 El enojo es una emoción válida. ¿Qué fue lo que pasó exactamente?",
                "Está bien sentirse así. 💪 El enojo puede ser energía. ¿Hay algo constructivo que puedas hacer con esa emoción?",
                "Respira hondo conmigo. 🔥 El fuego del enojo puede iluminar lo que necesita cambiar. ¿Qué necesitas en este momento?",
                "A veces necesitamos sacar el enojo de forma saludable. 🥊 ¿Has probado escribir una carta que nunca enviarás? Ayuda muchísimo."
            ))
        }

        // ============================================
        // 6. INSOMNIO / SUEÑO
        // ============================================
        if (containsAny(msg, listOf("dormir", "insomnio", "no duermo", "desvelado", "desvelada", "pesadillas", "cansado", "cansada", "agotado", "agotada"))) {
            return getRandomResponse(listOf(
                "El sueño es tan importante. 🌙 ¿Has probado nuestro ejercicio 'Sueño Profundo'? Está en la sección de respiración.",
                "Crear una rutina ayuda. 🌜 Intenta: sin pantallas 30 min antes, un té tibio, luz tenue y respiraciones lentas.",
                "A veces la mente no se apaga. 🧠 ¿Quieres intentar vaciar tus pensamientos aquí? Escríbeme todo lo que te preocupa.",
                "El cansancio acumulado es real. 😴 Sé amable contigo mismo/a. Mañana será un nuevo día."
            ))
        }

        // ============================================
        // 7. AUTOESTIMA
        // ============================================
        if (containsAny(msg, listOf("no valgo", "inútil", "inutil", "feo", "fea", "gordo", "gorda", "no puedo", "fracaso", "fracasado", "no sirvo", "insuficiente"))) {
            return getRandomResponse(listOf(
                "Eso que dices no es verdad. 💎 Eres valioso/a simplemente por ser quien eres. No necesitas demostrar nada.",
                "Las palabras que nos decimos importan mucho. 🦋 ¿Le dirías eso a un amigo querido? Trátate con la misma compasión.",
                "Todos tenemos días de duda. Pero mira todo lo que has superado para estar aquí hoy. 🌟 Eso es fuerza real.",
                "Tu valor no depende de logros, apariencia ni opiniones externas. 💝 Eres suficiente. Exactamente como eres."
            ))
        }

        // ============================================
        // 8. BULLYING / ACOSO
        // ============================================
        if (containsAny(msg, listOf("bullying", "acoso", "me molestan", "se burlan", "me pegan", "me ignoran", "rechazo", "marginado"))) {
            return getRandomResponse(listOf(
                "Lamento muchísimo que estés pasando por eso. 💔 No es tu culpa. Nadie merece ser tratado así. ¿Se lo has contado a algún adulto de confianza?",
                "El acoso dice más del acosador que de ti. 🛡️ Eres valioso/a y mereces respeto. ¿Hay algún docente o familiar que pueda ayudarte?",
                "No estás solo/a en esto. 🤝 Muchos han pasado por lo mismo. Lo importante es no quedarse callado/a. ¿Quieres que busquemos juntos a quién contarle?",
                "Tu bienestar es lo primero. 💪 A veces alejarse y buscar ayuda profesional es el acto más valiente. ¿Conoces las líneas de ayuda en la sección de emergencia?"
            ))
        }

        // ============================================
        // 9. EMERGENCIA / CRISIS
        // ============================================
        if (containsAny(msg, listOf("suicidio", "suicidar", "morir", "muerte", "lastimar", "lastimarme", "desaparecer", "no quiero vivir", "autolesión", "cortarme"))) {
            return """🆘 ESCUCHA: Lo que sientes ahora es real, pero NO es permanente. 
            
            Por favor, contacta AHORA MISMO a estas líneas gratuitas:
            
            📞 Línea de la Vida (México): 800-911-2000
            📞 SAPTEL: 55-5259-8121
            📞 Línea Diversa: 55-5658-1111
            
            También puedes:
            - Hablar con un adulto de confianza AHORA
            - Ir a la sala de emergencias más cercana
            - Llamar al 911
            
            💚 Te necesitamos aquí. El mundo es mejor contigo. Esto pasará."""
        }

        // ============================================
        // 10. EJERCICIOS DE RESPIRACIÓN
        // ============================================
        if (containsAny(msg, listOf("respirar", "respiración", "calma", "relajarme", "tranquilo", "tranquila", "relax"))) {
            return getRandomResponse(listOf(
                "Vamos a respirar juntos. 🌬️ Inhala contando hasta 4... mantén el aire 4 segundos... exhala lentamente en 6. Repite 3 veces. ¿Cómo te sientes?",
                "La respiración es tu ancla. ⚓ ¿Quieres ir a la sección de ejercicios guiados? Tenemos 7 opciones diferentes.",
                "Prueba esto: tapa tu fosa nasal derecha, inhala por la izquierda. Luego tapa la izquierda y exhala por la derecha. Esto equilibra el sistema nervioso. 🧘"
            ))
        }

        // ============================================
        // 11. PREGUNTAS SOBRE LA APP
        // ============================================
        if (containsAny(msg, listOf("cómo funciona", "que haces", "quién eres", "quien eres", "ayuda", "funciones", "qué puedes hacer", "que puedes hacer"))) {
            return """🌿 ¡Soy Sana, tu asistente emocional! Puedo:
            
            💬 Escucharte y conversar sobre cómo te sientes
            🫁 Guiarte en ejercicios de respiración
            📝 Motivarte a escribir en tu diario emocional
            🆘 Conectarte con líneas de ayuda
            🎮 Recomendarte juegos relajantes
            
            ¿Qué necesitas en este momento?"""
        }

        // ============================================
        // 12. DESPEDIDA
        // ============================================
        if (containsAny(msg, listOf("adiós", "adios", "bye", "hasta luego", "nos vemos", "me voy", "gracias por todo"))) {
            return getRandomResponse(listOf(
                "Cuídate mucho. 🌟 Recuerda que siempre puedes volver cuando me necesites. ¡Hasta pronto!",
                "Gracias por compartir conmigo. 🤍 Eres más fuerte de lo que crees. Nos vemos pronto.",
                "¡Hasta luego! 💫 No olvides respirar profundo si algo te agobia. Estaré aquí cuando quieras volver.",
                "Que tengas un hermoso día. 🌸 Recuerda: eres importante, eres valioso/a, eres suficiente."
            ))
        }

        // ============================================
        // 13. RESPUESTA POR DEFECTO (empática)
        // ============================================
        return getRandomResponse(listOf(
            "Te escucho con atención. 💭 Cuéntame más sobre eso, estoy aquí para ti sin juzgarte.",
            "Eso que compartes es importante. 🌿 ¿Cómo te hace sentir? A veces ponerlo en palabras ya ayuda.",
            "Gracias por confiar en mí. 🤍 ¿Hay algo más que quieras contarme sobre esto?",
            "Entiendo. 🫂 Cada persona vive las cosas de manera única. ¿Qué necesitarías en este momento?",
            "Sigue compartiendo, estoy aquí. 💜 A veces solo necesitamos ser escuchados. ¿Cómo puedo apoyarte mejor?",
            "Eso suena importante para ti. 🌟 ¿Quieres que exploremos esto juntos con más profundidad?"
        ))
    }

    /**
     * Verifica si el mensaje contiene alguna de las palabras clave
     */
    private fun containsAny(text: String, words: List<String>): Boolean {
        return words.any { word ->
            // Buscar la palabra como palabra completa (no como parte de otra)
            Regex("\\b${Regex.escape(word)}\\b").containsMatchIn(text) ||
            // O al inicio/fin con signos
            text.contains(word, ignoreCase = true)
        }
    }

    /**
     * Retorna una respuesta aleatoria de la lista para variar
     */
    private fun getRandomResponse(responses: List<String>): String {
        return responses[Random().nextInt(responses.size)]
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ChatMessage(
    val role: String,
    val content: String,
    val timestamp: String
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)