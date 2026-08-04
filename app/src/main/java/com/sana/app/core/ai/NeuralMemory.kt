package com.sana.app.core.ai

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.util.*

data class UserProfile(
    var preferredName: String = "",
    val interests: MutableList<String> = mutableListOf(),
    val moodHistory: MutableList<MoodEntry> = mutableListOf(),
    val conversationTopics: MutableMap<String, Int> = mutableMapOf(),
    val sensitiveTopics: MutableList<String> = mutableListOf(),
    val achievements: MutableList<String> = mutableListOf(),
    val firstSeen: Long = System.currentTimeMillis(),
    var lastSeen: Long = System.currentTimeMillis(),
    var totalConversations: Int = 0,
    var emotionalGrowth: Float = 0f
)

data class MoodEntry(val mood: String, val intensity: Int, val timestamp: Long, val context: String)

class NeuralMemory(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("sana_neural_memory", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun loadProfile(userId: String): UserProfile {
        val json = prefs.getString("profile_$userId", null)
        return if (json != null) {
            try { gson.fromJson(json, UserProfile::class.java) } catch (e: Exception) { UserProfile() }
        } else UserProfile()
    }
    
    fun saveProfile(userId: String, profile: UserProfile) {
        prefs.edit().putString("profile_$userId", gson.toJson(profile)).apply()
    }
    
    fun rememberMood(userId: String, mood: String, intensity: Int, context: String) {
        var profile = loadProfile(userId)
        profile.moodHistory.add(MoodEntry(mood, intensity, System.currentTimeMillis(), context))
        if (profile.moodHistory.size > 100) profile.moodHistory.removeAt(0)
        profile.lastSeen = System.currentTimeMillis()
        saveProfile(userId, profile)
    }
    
    fun addInterest(userId: String, interest: String) {
        var profile = loadProfile(userId)
        if (!profile.interests.contains(interest)) profile.interests.add(interest)
        saveProfile(userId, profile)
    }
    
    fun getEmotionalPattern(userId: String): Map<String, Int> {
        val profile = loadProfile(userId)
        return profile.moodHistory.groupBy { it.mood }.mapValues { it.value.size }
    }
    
    fun getDominantMood(userId: String): String {
        val pattern = getEmotionalPattern(userId)
        return pattern.maxByOrNull { it.value }?.key ?: "NEUTRAL"
    }
    
    fun getEmotionalGrowth(userId: String): Float {
        val profile = loadProfile(userId)
        val recent = profile.moodHistory.takeLast(10)
        if (recent.isEmpty()) return 0f
        val positive = recent.count { it.mood in listOf("HAPPY", "CALM") }
        return positive.toFloat() / recent.size
    }
}
