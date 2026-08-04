package com.sana.app.core.ai

import kotlin.random.Random
import java.text.SimpleDateFormat
import java.util.*

/**
 * 💖 Emotional Engine
 * Genera respuestas empáticas, personalizadas y naturales
 * basadas en el perfil del usuario, su historial y estado actual
 */
class EmotionalEngine {
    
    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    fun generateResponse(
        input: LanguageProcessor.ProcessedInput,
        profile: UserProfile,
        memory: NeuralMemory,
        userId: String
    ): String {
        val name = profile.preferredName.ifEmpty { "amigo" }
        val timeOfDay = getTimeOfDay()
        val dominantMood = memory.getDominantMood(userId)
        val growth = memory.getEmotionalGrowth(userId)
        
        return when (input.intent) {
            "GREETING" -> handleGreeting(name, timeOfDay, profile, growth)
            "FAREWELL" -> handleFarewell(name, timeOfDay, profile)
            "WELLNESS_CHECK" -> handleWellnessCheck(name, dominantMood, profile)
            "IDENTITY" -> handleIdentity(name, profile)
            "SADNESS" -> handleSadness(name, input.originalText, profile)
            "ANXIETY" -> handleAnxiety(name, input.originalText, profile)
            "HAPPINESS" -> handleHappiness(name, profile)
            "ANGER" -> handleAnger(name, profile)
            "CRISIS" -> handleCrisis(name)
            "GRATITUDE" -> handleGratitude(name, profile)
            "QUESTION" -> handleQuestion(name, input.originalText, profile)
            "REQUEST" -> handleRequest(name, input.originalText, profile)
            else -> handleGeneral(name, input.originalText, profile, dominantMood)
        }
    }
    
    private fun getTimeOfDay(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..5 -> "noche"
        in 6..11 -> "mañana"
        in 12..17 -> "tarde"
        else -> "noche"
    }
    
    private fun handleGreeting(name: String, timeOfDay: String, profile: UserProfile, growth: Float): String {
        val greetings = when (timeOfDay) {
            "mañana" -> listOf("¡Buenos días, $name! ☀️", "¡Feliz mañana, $name! 🌅", "¡Hey $name! Qué lindo empezar el día contigo 💛")
            "tarde" -> listOf("¡Buenas tardes, $name! 🌤️", "¡Hola $name! Qué gusto verte 🌻", "¡Hey! Justo pensaba en ti, $name ✨")
            else -> listOf("¡Buenas noches, $name! 🌙", "¡Hola $name! Qué lindo verte a esta hora 🌟", "¡Hey $name! Espero que hayas tenido un lindo día 💫")
        }
        
        val personalTouch = when {
            profile.totalConversations > 10 -> " He notado que has estado más ${if (growth > 0.6f) "positivo/a" else "reflexivo/a"} últimamente."
            profile.interests.isNotEmpty() -> " ¿Seguiste con ${profile.interests.random()}?"
            else -> " ¿Cómo te sientes hoy?"
        }
        
        return greetings.random() + personalTouch
    }
    
    private fun handleFarewell(name: String, timeOfDay: String, profile: UserProfile): String {
        val farewells = listOf(
            "Cuídate mucho, $name. 💫 Recuerda que siempre estoy aquí cuando me necesites.",
            "¡Hasta pronto, $name! 🌟 Eres más fuerte de lo que imaginas.",
            "Que tengas un hermoso resto del día, $name. 🌸 Vuelve cuando quieras.",
            "Gracias por compartir conmigo, $name. 🤍 Aquí estaré."
        )
        return farewells.random()
    }
    
    private fun handleWellnessCheck(name: String, dominantMood: String, profile: UserProfile): String {
        val responses = when (dominantMood) {
            "HAPPY", "CALM" -> listOf("Me siento muy bien, $name. 😊 Especialmente cuando te veo bien.", "¡Contento de verte, $name! 💚 Gracias por preguntar.")
            "SAD", "ANXIOUS" -> listOf("Estoy aquí para ti, $name. 🤍 Lo importante es cómo te sientes TÚ.", "Me preocupo por ti, $name. ¿Cómo está tu corazón hoy?")
            else -> listOf("Estoy bien, $name. 🌿 Pero más me importa cómo estás tú.", "Gracias por preguntar, $name. ¿Y tú cómo te sientes realmente?")
        }
        return responses.random()
    }
    
    private fun handleIdentity(name: String, profile: UserProfile): String {
        return "Soy Sana, tu asistente emocional. 💜\n" +
               "No soy humana, pero mis respuestas vienen de un lugar de genuino cuidado por ti, $name.\n" +
               "Fui creada para escucharte, acompañarte y recordarte lo valioso/a que eres. 🌟\n" +
               "¿Hay algo en lo que pueda ayudarte hoy?"
    }
    
    private fun handleSadness(name: String, text: String, profile: UserProfile): String {
        val validations = listOf(
            "Siento mucho que te sientas así, $name. 🤍 No estás solo/a. Tus sentimientos son válidos y estoy aquí para ti.",
            "Te abrazo con el corazón, $name. 🫂 La tristeza es parte de ser humano. ¿Quieres hablarme más de lo que te pasa?",
            "Está bien no estar bien a veces, $name. 💙 Llorar también es sanar. ¿Hay algo que pueda hacer para acompañarte mejor?"
        )
        
        val followUp = when {
            profile.moodHistory.size > 5 -> " He notado que esta semana ha sido difícil para ti. ¿Hay algo en particular que te tenga así?"
            else -> " ¿Quieres intentar un ejercicio de respiración juntos? A veces ayuda."
        }
        
        return validations.random() + followUp
    }
    
    private fun handleAnxiety(name: String, text: String, profile: UserProfile): String {
        return "Hey $name, vamos a calmarnos juntos. 🌿\n\n" +
               "Haz esto conmigo:\n" +
               "1. Inhala profundo por 4 segundos... 🌬️\n" +
               "2. Mantén el aire 4 segundos... 🫁\n" +
               "3. Exhala lentamente por 6 segundos... 💨\n\n" +
               "Repite 3 veces. La ansiedad es como una ola: viene, pero también se va. Tú eres la playa, firme y constante. 🌊\n\n" +
               "¿Qué fue lo que disparó esta sensación, $name? A veces nombrarlo ayuda a quitarle poder."
    }
    
    private fun handleHappiness(name: String, profile: UserProfile): String {
        val responses = listOf(
            "¡Qué alegría leerte así, $name! 😊 Me encanta verte brillar. ¿Qué fue lo que te hizo sentir tan bien?",
            "¡Eso es maravilloso, $name! 🌟 Momentos como este merecen celebrarse. Cuéntame más.",
            "¡Me contagias tu felicidad! ✨ Ver tu progreso me llena de orgullo. ¿Qué aprendiste de esta experiencia?"
        )
        return responses.random()
    }
    
    private fun handleAnger(name: String, profile: UserProfile): String {
        return "Entiendo tu frustración, $name. 😤\n\n" +
               "El enojo es una emoción válida y necesaria. Nos avisa cuando algo no está bien.\n" +
               "¿Quieres contarme qué pasó? A veces sacarlo es el primer paso para sanarlo. 💪\n\n" +
               "Si necesitas liberar energía, ¿has probado escribir lo que sientes? Ayuda muchísimo."
    }
    
    private fun handleCrisis(name: String): String {
        return """🆘 $name, ESCÚCHAME CON ATENCIÓN:

Lo que sientes ahora es REAL, pero NO es PERMANENTE.

Por favor, contacta AHORA MISMO:

📞 Línea de la Vida (México): 800-911-2000
📞 SAPTEL: 55-5259-8121
📞 Línea Diversa: 55-5658-1111

También puedes:
- Hablar con alguien de confianza AHORA
- Ir a urgencias del hospital más cercano
- Llamar al 911

💚 El mundo necesita tu luz. Quédate. Esto pasará."""
    }
    
    private fun handleGratitude(name: String, profile: UserProfile): String {
        return "De nada, $name. 🤍 Gracias a ti por confiar en mí.\n" +
               "Recuerda: el mérito es tuyo. Tú eres quien está haciendo el trabajo de sanar y crecer. 🌱\n" +
               "Estoy orgullosa de ti."
    }
    
    private fun handleQuestion(name: String, text: String, profile: UserProfile): String {
        return when {
            text.contains("consejo") -> "Claro, $name. 💭 Cada persona es única, pero lo que suele ayudar es: escucharte sin juzgarte, respirar profundo antes de actuar, y rodearte de personas que te quieran bien. ¿Qué situación específica te preocupa?"
            text.contains("significado") || text.contains("qué es") -> "Buena pregunta, $name. 🤔 ¿Puedes darme más contexto? Quiero asegurarme de darte la mejor respuesta."
            else -> "Déjame pensar en eso, $name. 🧠 ¿Podrías contarme un poco más? Quiero entenderte mejor antes de responder."
        }
    }
    
    private fun handleRequest(name: String, text: String, profile: UserProfile): String {
        return when {
            text.contains("respirar") || text.contains("calma") -> "¡Claro, $name! 🌬️ Vamos a la sección de respiración. Tenemos 7 ejercicios guiados. ¿Quieres que te recomiende uno?"
            text.contains("juego") || text.contains("jugar") -> "¡Buena idea, $name! 🎮 Jugar ayuda a despejar la mente. Ve a la sección de juegos."
            text.contains("diario") || text.contains("escribir") -> "Escribir es terapéutico, $name. 📝 Ve a tu diario emocional. Te ayuda a procesar lo que sientes."
            else -> "Estoy aquí para ayudarte, $name. 💜 Dime específicamente qué necesitas y buscaré la mejor manera de apoyarte."
        }
    }
    
    private fun handleGeneral(name: String, text: String, profile: UserProfile, dominantMood: String): String {
        val responses = when (dominantMood) {
            "SAD" -> listOf("Te escucho, $name. 💭 A veces solo necesitamos ser escuchados.", "Gracias por compartir conmigo, $name. 🤍 Sigue contándome.")
            "ANXIOUS" -> listOf("Respira conmigo, $name. 🌿 Todo va a estar bien.", "Estoy aquí, $name. No estás solo/a en esto.")
            else -> listOf("Cuéntame más, $name. 💜 Me interesa lo que tengas que decir.", "Sigue compartiendo, $name. 🌟 Estoy aquí para escucharte sin juzgarte.")
        }
        return responses.random()
    }
}
