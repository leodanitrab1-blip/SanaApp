package com.sana.app.core.database.dao

import androidx.room.*
import com.sana.app.core.database.entities.BreathingSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * 🌿 SANA - DAO de Sesiones de Respiración
 */
@Dao
interface BreathingSessionDao {

    @Query("SELECT * FROM breathing_sessions WHERE user_id = :userId ORDER BY timestamp DESC")
    fun getSessionsForUser(userId: Long): Flow<List<BreathingSessionEntity>>

    @Query("SELECT * FROM breathing_sessions WHERE user_id = :userId AND exercise_id = :exerciseId ORDER BY timestamp DESC")
    fun getSessionsForExercise(userId: Long, exerciseId: Int): Flow<List<BreathingSessionEntity>>

    @Query("SELECT COUNT(*) FROM breathing_sessions WHERE user_id = :userId")
    fun getTotalSessions(userId: Long): Flow<Int>

    @Query("SELECT SUM(duration_seconds) FROM breathing_sessions WHERE user_id = :userId")
    fun getTotalBreathingTime(userId: Long): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: BreathingSessionEntity): Long

    @Query("DELETE FROM breathing_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)
}