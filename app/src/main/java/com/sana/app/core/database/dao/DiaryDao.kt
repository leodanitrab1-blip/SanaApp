package com.sana.app.core.database.dao

import androidx.room.*
import com.sana.app.core.database.entities.DiaryEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * 🌿 SANA - DAO de Diario Emocional
 * 
 * Gestiona las entradas del diario emocional de los usuarios.
 * Permite registrar, consultar y analizar estados de ánimo.
 */
@Dao
interface DiaryDao {

    // ============ CONSULTAS DE LECTURA ============

    /**
     * Obtener todas las entradas de un usuario, ordenadas por fecha descendente
     */
    @Query("""
        SELECT * FROM diary_entries 
        WHERE user_id = :userId 
        ORDER BY timestamp DESC
    """)
    fun getEntriesForUser(userId: Long): Flow<List<DiaryEntryEntity>>

    /**
     * Obtener entradas de un usuario en un rango de fechas
     */
    @Query("""
        SELECT * FROM diary_entries 
        WHERE user_id = :userId 
        AND timestamp BETWEEN :startDate AND :endDate
        ORDER BY timestamp DESC
    """)
    fun getEntriesForUserInDateRange(
        userId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<List<DiaryEntryEntity>>

    /**
     * Obtener entradas por estado de ánimo específico
     */
    @Query("""
        SELECT * FROM diary_entries 
        WHERE user_id = :userId AND mood = :mood
        ORDER BY timestamp DESC
    """)
    fun getEntriesByMood(userId: Long, mood: String): Flow<List<DiaryEntryEntity>>

    /**
     * Obtener entrada específica por ID
     */
    @Query("SELECT * FROM diary_entries WHERE id = :entryId LIMIT 1")
    suspend fun getEntryById(entryId: Long): DiaryEntryEntity?

    /**
     * Contar entradas por estado de ánimo (para estadísticas)
     * Retorna mapa de mood -> cantidad
     */
    @Query("""
        SELECT mood, COUNT(*) as count 
        FROM diary_entries 
        WHERE user_id = :userId 
        GROUP BY mood 
        ORDER BY count DESC
    """)
    fun getMoodStats(userId: Long): Flow<List<MoodStat>>

    /**
     * Obtener la última entrada del usuario
     */
    @Query("""
        SELECT * FROM diary_entries 
        WHERE user_id = :userId 
        ORDER BY timestamp DESC 
        LIMIT 1
    """)
    suspend fun getLastEntry(userId: Long): DiaryEntryEntity?

    /**
     * Buscar en el contenido de las entradas
     */
    @Query("""
        SELECT * FROM diary_entries 
        WHERE user_id = :userId 
        AND (content LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%')
        ORDER BY timestamp DESC
    """)
    fun searchEntries(userId: Long, query: String): Flow<List<DiaryEntryEntity>>

    /**
     * Obtener racha de días consecutivos escribiendo
     */
    @Query("""
        SELECT DISTINCT strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch') as date_str
        FROM diary_entries 
        WHERE user_id = :userId 
        ORDER BY date_str DESC 
        LIMIT :limit
    """)
    suspend fun getRecentEntryDates(userId: Long, limit: Int = 30): List<String>

    // ============ OPERACIONES DE ESCRITURA ============

    /**
     * Insertar nueva entrada del diario
     * @return ID de la entrada creada
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DiaryEntryEntity): Long

    /**
     * Actualizar entrada existente
     */
    @Update
    suspend fun updateEntry(entry: DiaryEntryEntity)

    /**
     * Actualizar solo el contenido de una entrada
     */
    @Query("""
        UPDATE diary_entries 
        SET content = :content, modified_at = :modifiedAt 
        WHERE id = :entryId
    """)
    suspend fun updateEntryContent(entryId: Long, content: String, modifiedAt: Long)

    // ============ OPERACIONES DE ELIMINACIÓN ============

    /**
     * Eliminar entrada por ID
     */
    @Delete
    suspend fun deleteEntry(entry: DiaryEntryEntity)

    /**
     * Eliminar entrada por ID usando query
     */
    @Query("DELETE FROM diary_entries WHERE id = :entryId")
    suspend fun deleteEntryById(entryId: Long)

    /**
     * Eliminar todas las entradas de un usuario
     */
    @Query("DELETE FROM diary_entries WHERE user_id = :userId")
    suspend fun deleteAllEntriesForUser(userId: Long)
}

/**
 * Clase auxiliar para estadísticas de estados de ánimo
 * Room la usa para mapear resultados de agregación
 */
data class MoodStat(
    val mood: String,
    val count: Int
)