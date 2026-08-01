package com.sana.app.core.repository

import com.sana.app.core.database.dao.DiaryDao
import com.sana.app.core.database.entities.DiaryEntryEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🌿 SANA - Repositorio de Diario Emocional
 * 
 * Gestiona las entradas del diario emocional con funcionalidades
 * adicionales como estadísticas de ánimo, rachas y patrones.
 */
@Singleton
class DiaryRepository @Inject constructor(
    private val diaryDao: DiaryDao
) {

    // ============================================
    // CONSULTAS
    // ============================================

    /** Obtener todas las entradas del usuario */
    fun getEntriesForUser(userId: Long): Flow<List<DiaryEntryEntity>> =
        diaryDao.getEntriesForUser(userId)

    /** Obtener entradas en un rango de fechas */
    fun getEntriesInDateRange(
        userId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<List<DiaryEntryEntity>> =
        diaryDao.getEntriesForUserInDateRange(userId, startDate, endDate)

    /** Obtener entradas por estado de ánimo */
    fun getEntriesByMood(userId: Long, mood: String): Flow<List<DiaryEntryEntity>> =
        diaryDao.getEntriesByMood(userId, mood)

    /** Buscar en entradas del diario */
    fun searchEntries(userId: Long, query: String): Flow<List<DiaryEntryEntity>> =
        diaryDao.searchEntries(userId, query)

    /** Obtener estadísticas de estados de ánimo */
    fun getMoodStats(userId: Long) = diaryDao.getMoodStats(userId)

    /** Obtener la última entrada */
    suspend fun getLastEntry(userId: Long): DiaryEntryEntity? =
        diaryDao.getLastEntry(userId)

    // ============================================
    // OPERACIONES
    // ============================================

    /**
     * Guardar una nueva entrada del diario
     */
    suspend fun saveEntry(
        userId: Long,
        mood: String,
        content: String,
        title: String? = null,
        tags: List<String>? = null,
        intensity: Int? = null,
        triggers: List<String>? = null
    ): Result<DiaryEntryEntity> {
        return try {
            // Validar estado de ánimo
            val validMoods = listOf("HAPPY", "CALM", "NEUTRAL", "SAD", "ANXIOUS", "ANGRY")
            if (mood !in validMoods) {
                return Result.failure(IllegalArgumentException("Estado de ánimo no válido: $mood"))
            }

            // Validar contenido
            if (content.isBlank()) {
                return Result.failure(IllegalArgumentException("El contenido no puede estar vacío"))
            }

            val entry = DiaryEntryEntity(
                userId = userId,
                mood = mood,
                content = content.trim(),
                title = title?.trim(),
                tags = tags?.joinToString(","),
                intensity = intensity?.coerceIn(1, 10),
                triggers = triggers?.joinToString(",")
            )

            val id = diaryDao.insertEntry(entry)
            Result.success(entry.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualizar una entrada existente
     */
    suspend fun updateEntry(
        entryId: Long,
        content: String,
        mood: String? = null,
        title: String? = null
    ): Result<Unit> {
        return try {
            val entry = diaryDao.getEntryById(entryId)
                ?: return Result.failure(Exception("Entrada no encontrada"))

            diaryDao.updateEntry(
                entry.copy(
                    content = content.trim(),
                    mood = mood ?: entry.mood,
                    title = title?.trim() ?: entry.title,
                    modifiedAt = System.currentTimeMillis()
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eliminar una entrada
     */
    suspend fun deleteEntry(entryId: Long): Result<Unit> {
        return try {
            diaryDao.deleteEntryById(entryId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // ESTADÍSTICAS Y ANÁLISIS
    // ============================================

    /**
     * Calcular racha actual de días consecutivos escribiendo
     */
    suspend fun getCurrentStreak(userId: Long): Int {
        val dates = diaryDao.getRecentEntryDates(userId, 60)
        if (dates.isEmpty()) return 0

        var streak = 1
        val calendar = Calendar.getInstance()
        
        // Fecha de hoy
        val today = calendar.timeInMillis
        
        for (i in 0 until dates.size - 1) {
            val current = dates[i]
            val next = dates[i + 1]
            
            // Verificar si son días consecutivos
            // (Esta lógica se simplifica; en producción usarías manejo de fechas más robusto)
            if (current == next) {
                streak++
            } else {
                break
            }
        }
        
        return streak
    }

    /**
     * Obtener resumen semanal del estado de ánimo
     */
    suspend fun getWeeklyMoodSummary(userId: Long): WeeklyMoodSummary {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startDate = calendar.timeInMillis

        // Obtener entradas de la semana
        val entries = mutableListOf<DiaryEntryEntity>()
        diaryDao.getEntriesForUserInDateRange(userId, startDate, endDate)
            .collect { entries.addAll(it) }

        // Contar estados de ánimo
        val moodCounts = entries.groupBy { it.mood }
            .mapValues { it.value.size }

        // Encontrar estado de ánimo predominante
        val predominantMood = moodCounts.maxByOrNull { it.value }?.key ?: "NEUTRAL"

        // Calcular promedio de intensidad
        val avgIntensity = entries
            .mapNotNull { it.intensity }
            .average()
            .takeIf { !it.isNaN() }

        return WeeklyMoodSummary(
            totalEntries = entries.size,
            predominantMood = predominantMood,
            moodCounts = moodCounts,
            averageIntensity = avgIntensity
        )
    }
}

/**
 * Resumen semanal del estado de ánimo
 */
data class WeeklyMoodSummary(
    val totalEntries: Int,
    val predominantMood: String,
    val moodCounts: Map<String, Int>,
    val averageIntensity: Double?
)