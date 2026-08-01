package com.sana.app.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 🌿 SANA - Entidad Sesión de Respiración
 * 
 * Registro de las sesiones de ejercicios de respiración
 * realizadas por los usuarios para seguimiento.
 */
@Entity(
    tableName = "breathing_sessions",
    indices = [
        Index(value = ["user_id", "timestamp"]),
        Index(value = ["exercise_id"])
    ]
)
data class BreathingSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** ID del usuario que realizó el ejercicio */
    @ColumnInfo(name = "user_id")
    val userId: Long,

    /** ID del ejercicio realizado (1-7) */
    @ColumnInfo(name = "exercise_id")
    val exerciseId: Int,

    /** Nombre del ejercicio */
    @ColumnInfo(name = "exercise_name")
    val exerciseName: String,

    /** Duración total en segundos */
    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Int,

    /** Número de rondas completadas */
    @ColumnInfo(name = "rounds_completed")
    val roundsCompleted: Int,

    /** Si se completó el ejercicio completo */
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = true,

    /** Estado de ánimo antes del ejercicio */
    @ColumnInfo(name = "mood_before")
    val moodBefore: String? = null,

    /** Estado de ánimo después del ejercicio */
    @ColumnInfo(name = "mood_after")
    val moodAfter: String? = null,

    /** Timestamp de la sesión */
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)