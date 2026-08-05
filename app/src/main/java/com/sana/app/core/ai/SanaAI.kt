package com.sana.app.core.ai

import android.content.Context

class SanaAI(private val context: Context) {
    private val memory = NeuralMemory(context)
    private val processor = LanguageProcessor()
    private val engine = EmotionalEngine()
    private val notifier = NotificationEngine(context)

    fun chat(userId: String, message: String): String {
        return try {
            val profile = memory.loadProfile(userId)
            val processed = processor.process(message, profile.preferredName, profile)

            if (processed.entities.containsKey("name")) {
                val newName = processed.entities["name"] ?: ""
                if (newName.isNotEmpty()) profile.preferredName = newName
            }

            val mood = when (processed.sentiment) {
                "POSITIVE" -> "HAPPY"
                "NEGATIVE" -> "SAD"
                "ANXIOUS" -> "ANXIOUS"
                else -> "NEUTRAL"
            }

            memory.rememberMood(userId, mood, processed.urgency, message.take(50))
            profile.totalConversations = profile.totalConversations + 1
            profile.lastSeen = System.currentTimeMillis()
            memory.saveProfile(userId, profile)

            engine.generateResponse(processed, profile, memory, userId)
        } catch (e: Exception) {
            e.printStackTrace()
            "Estoy aquí para ti 💜 Cuéntame cómo te sientes hoy."
        }
    }

    fun getProfile(userId: String) = memory.loadProfile(userId)
    fun getEmotionalGrowth(userId: String) = memory.getEmotionalGrowth(userId)
}
