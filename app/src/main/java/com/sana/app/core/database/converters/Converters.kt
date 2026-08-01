package com.sana.app.core.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

/**
 * 🌿 SANA - Convertidores de Tipos para Room
 * 
 * Room no puede almacenar tipos complejos directamente en SQLite.
 * Estos convertidores transforman tipos Kotlin/Java a formatos
 * que SQLite entiende (String, Long, Int) y viceversa.
 * 
 * Tipos convertidos:
 * - List<String> <-> JSON String
 * - Map<String, String> <-> JSON String
 * - Date <-> Long (epoch millis)
 * - List<Long> <-> JSON String
 */
class Converters {
    
    private val gson = Gson()

    // ============ List<String> ============
    
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value ?: emptyList<String>())
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(value, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ============ List<Long> ============
    
    @TypeConverter
    fun fromLongList(value: List<Long>?): String {
        return gson.toJson(value ?: emptyList<Long>())
    }

    @TypeConverter
    fun toLongList(value: String): List<Long> {
        val type = object : TypeToken<List<Long>>() {}.type
        return try {
            gson.fromJson(value, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ============ Map<String, String> ============
    
    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String {
        return gson.toJson(value ?: emptyMap<String, String>())
    }

    @TypeConverter
    fun toStringMap(value: String): Map<String, String> {
        val type = object : TypeToken<Map<String, String>>() {}.type
        return try {
            gson.fromJson(value, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    // ============ Date ============
    
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    // ============ Any (genérico) ============
    
    @TypeConverter
    fun fromAny(value: Any?): String {
        return gson.toJson(value ?: "")
    }

    @TypeConverter
    fun toAny(value: String): Any {
        return try {
            gson.fromJson(value, Any::class.java) ?: value
        } catch (e: Exception) {
            value
        }
    }
}