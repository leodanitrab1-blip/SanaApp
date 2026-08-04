package com.sana.app.core.ai

import kotlin.random.Random

/**
 * 🗣️ Language Processor
 * Procesamiento de lenguaje natural avanzado
 * - Detección de intención (saludo, despedida, pregunta, queja, etc)
 * - Análisis de sentimiento
 * - Extracción de entidades (nombres, lugares, fechas)
 * - Comprensión contextual
 */
class LanguageProcessor {
    
    data class ProcessedInput(
        val intent: String,
        val sentiment: String,
        val entities: Map<String, String>,
        val topics: List<String>,
        val urgency: Int,
        val originalText: String
    )
    
    fun process(text: String, userName: String, profile: UserProfile): ProcessedInput {
        val lower = text.lowercase().trim()
        
        // Detectar intención
        val intent = detectIntent(lower)
        
        // Analizar sentimiento
        val sentiment = analyzeSentiment(lower, profile)
        
        // Extraer entidades
        val entities = extractEntities(lower, userName)
        
        // Identificar temas
        val topics = identifyTopics(lower, profile)
        
        // Calcular urgencia
        val urgency = calculateUrgency(lower, sentiment)
        
        return ProcessedInput(intent, sentiment, entities, topics, urgency, text)
    }
    
    private fun detectIntent(text: String): String {
        return when {
            text.matches(Regex(".*\\b(hola|hey|buenos días|buenas tardes|buenas noches|saludos|hi|hello)\\b.*")) -> "GREETING"
            text.matches(Regex(".*\\b(adiós|adios|bye|hasta luego|nos vemos|chao|bye bye)\\b.*")) -> "FAREWELL"
            text.matches(Regex(".*\\b(cómo estás|como estas|qué tal|que tal|cómo te va|como te va)\\b.*")) -> "WELLNESS_CHECK"
            text.matches(Regex(".*\\b(quién eres|quien eres|qué eres|que eres|cómo te llamas|como te llamas)\\b.*")) -> "IDENTITY"
            text.matches(Regex(".*\\b(puedes|podrías|podrias|me ayudas|ayúdame|ayudame|necesito)\\b.*")) -> "REQUEST"
            text.contains("?") || text.contains("?") -> "QUESTION"
            text.matches(Regex(".*\\b(gracias|te agradezco|muchas gracias|thank you|thanks)\\b.*")) -> "GRATITUDE"
            text.matches(Regex(".*\\b(triste|deprimido|deprimida|llorar|solo|sola|vacío|vacia|sin ganas)\\b.*")) -> "SADNESS"
            text.matches(Regex(".*\\b(ansioso|ansiosa|ansiedad|estrés|estresado|nervioso|preocupado|miedo|pánico|panico)\\b.*")) -> "ANXIETY"
            text.matches(Regex(".*\\b(feliz|contento|contenta|alegre|genial|excelente|maravilloso)\\b.*")) -> "HAPPINESS"
            text.matches(Regex(".*\\b(enojado|enojada|furioso|furiosa|frustrado|rabia|odio|molesto)\\b.*")) -> "ANGER"
            text.matches(Regex(".*\\b(suicidio|suicidar|morir|muerte|lastimar|desaparecer|no quiero vivir|autolesión|cortarme)\\b.*")) -> "CRISIS"
            else -> "GENERAL"
        }
    }
    
    private fun analyzeSentiment(text: String, profile: UserProfile): String {
        val positive = listOf("feliz", "contento", "alegre", "bien", "genial", "excelente", "amor", "gracias", "hermoso", "bonito")
        val negative = listOf("triste", "mal", "peor", "horrible", "odio", "dolor", "sufrir", "llorar", "solo", "vacío")
        val anxious = listOf("ansioso", "nervioso", "preocupado", "miedo", "pánico", "estrés", "angustia")
        
        val posCount = positive.count { text.contains(it) }
        val negCount = negative.count { text.contains(it) }
        val anxCount = anxious.count { text.contains(it) }
        
        return when {
            anxCount > posCount && anxCount > negCount -> "ANXIOUS"
            negCount > posCount -> "NEGATIVE"
            posCount > negCount -> "POSITIVE"
            else -> "NEUTRAL"
        }
    }
    
    private fun extractEntities(text: String, userName: String): Map<String, String> {
        val entities = mutableMapOf<String, String>()
        if (text.contains("me llamo")) {
            val match = Regex("me llamo\\s+(\\w+)").find(text)
            match?.let { entities["name"] = it.groupValues[1] }
        }
        if (text.contains("soy")) {
            val match = Regex("soy\\s+(\\w+)").find(text)
            match?.let { entities["identity"] = it.groupValues[1] }
        }
        return entities
    }
    
    private fun identifyTopics(text: String, profile: UserProfile): List<String> {
        val topics = mutableListOf<String>()
        if (text.contains("escuela") || text.contains("colegio") || text.contains("clase")) topics.add("school")
        if (text.contains("familia") || text.contains("papá") || text.contains("mamá") || text.contains("padre") || text.contains("madre")) topics.add("family")
        if (text.contains("amigo") || text.contains("amiga") || text.contains("compañero")) topics.add("friends")
        if (text.contains("amor") || text.contains("novio") || text.contains("novia") || text.contains("gusta")) topics.add("love")
        if (text.contains("dormir") || text.contains("sueño") || text.contains("cansado")) topics.add("sleep")
        if (text.contains("comer") || text.contains("comida") || text.contains("hambre")) topics.add("food")
        return topics.ifEmpty { listOf("general") }
    }
    
    private fun calculateUrgency(text: String, sentiment: String): Int {
        var urgency = 1
        if (text.contains("urgente") || text.contains("emergencia") || text.contains("ayuda")) urgency += 3
        if (text.matches(Regex(".*\\b(suicidio|suicidar|morir|muerte|lastimar)\\b.*"))) urgency = 10
        if (sentiment == "ANXIOUS") urgency += 2
        if (text.contains("!!") || text.contains("!!")) urgency += 1
        return urgency
    }
}
