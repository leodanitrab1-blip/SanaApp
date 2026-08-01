package com.sana.app.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 🌿 SANA - Entidad Entrada de Diario Emocional
 * 
 * Registro privado del estado emocional del usuario.
 * Cada entrada captura el ánimo, pensamientos y sentimientos.
 */
@Entity(
    tableName = "diary_entries",
    indices = [
        Index(value = ["user_id", "timestamp"]),
        Index(value = ["mood"])
    ]
)
data class DiaryEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** ID del usuario que creó la entrada */
    @ColumnInfo(name = "user_id")
    val userId: Long,

    /** 
     * Estado de ánimo (emoji + descripción):
     * HAPPY, CALM, NEUTRAL, SAD, ANXIOUS, ANGRY
     */
    @ColumnInfo(name = "mood")
    val mood: String,

    /** Contenido del diario (pensamientos, sentimientos) */
    @ColumnInfo(name = "content")
    val content: String,

    /** Título opcional para la entrada */
    @ColumnInfo(name = "title")
    val title: String? = null,

    /** Si la entrada es privada o puede compartirse */
    @ColumnInfo(name = "is_private", defaultValue = "1")
    val isPrivate: Boolean = true,

    /** Etiquetas para categorizar la entrada (JSON array) */
    @ColumnInfo(name = "tags")
    val tags: String? = null, // ["familia","escuela","amigos"]

    /** Intensidad de la emoción (1-10) */
    @ColumnInfo(name = "intensity")
    val intensity: Int? = null,

    /** Factores que influyeron en el estado de ánimo */
    @ColumnInfo(name = "triggers")
    val triggers: String? = null, // JSON array

    /** Timestamp de creación */
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    /** Timestamp de última modificación */
    @ColumnInfo(name = "modified_at")
    val modifiedAt: Long? = null
)