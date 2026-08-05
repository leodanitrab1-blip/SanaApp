package com.sana.app.core.ai

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SanaAI(private val context: Context) {
    private val memory = NeuralMemory(context)
    private val processor = LanguageProcessor()
    private val engine = EmotionalEngine()
    private val notifier = NotificationEngine(context)
    
    suspend fun initialize(userId: String): UserProfile = withContext(Dispatchers.IO) {
        val profile = memory.loadProfile(userId)
        notifier.createNotificationChannel()
        if (profile.preferredName.isNotEmpty()) {
            notifier.scheduleMotivationalNotifications(profile)
        }
        profile
    }
    
    suspend fun chat(userId: String, message: String): String = withContext(Dispatchers.IO) { try {
        var profile = memory.loadProfile(userId)
        val processed = processor.process(message, profile.preferredName, profile)
        learnFromInteraction(userId, message, processed, profile)
        engine.generateResponse(processed, profile, memory, userId)
        } catch (e: Exception) { "Estoy aquí para ti 💜 Cuéntame cómo te sientes." }
    }
    
    private fun learnFromInteraction(userId: String, message: String, processed: LanguageProcessor.ProcessedInput, profile: UserProfile) {
        if (processed.entities.containsKey("name")) {
            profile.preferredName = processed.entities["name"]!!
        }
        if (message.contains("me gusta") || message.contains("amo") || message.contains("disfruto")) {
            val interest = message.split("me gusta", "amo", "disfruto").lastOrNull()?.trim()?.take(30) ?: ""
            if (interest.isNotEmpty()) memory.addInterest(userId, interest)
        }
        val mood = when (processed.sentiment) { "POSITIVE" -> "HAPPY"; "NEGATIVE" -> "SAD"; "ANXIOUS" -> "ANXIOUS"; else -> "NEUTRAL" }
        memory.rememberMood(userId, mood, processed.urgency, message.take(50))
        profile.totalConversations++
        profile.lastSeen = System.currentTimeMillis()
        memory.saveProfile(userId, profile)
    }
    
    fun getProfile(userId: String): UserProfile = memory.loadProfile(userId)
    fun getEmotionalGrowth(userId: String): Float = memory.getEmotionalGrowth(userId)
}
