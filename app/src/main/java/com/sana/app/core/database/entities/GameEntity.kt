package com.sana.app.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 🌿 SANA - Entidad Juego
 * 
 * Juegos HTML/JavaScript/Python que los usuarios pueden jugar.
 * Categorizados por nivel educativo: general, primaria, secundaria.
 */
@Entity(
    tableName = "games",
    indices = [
        Index(value = ["category"]),
        Index(value = ["is_active"])
    ]
)
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Título del juego */
    @ColumnInfo(name = "title")
    val title: String,

    /** Descripción del juego */
    @ColumnInfo(name = "description")
    val description: String,

    /** 
     * Categoría del juego:
     * GENERAL, PRIMARY, SECONDARY
     */
    @ColumnInfo(name = "category")
    val category: String,

    /** URL del archivo del juego (GitHub raw o local) */
    @ColumnInfo(name = "file_url")
    val fileUrl: String,

    /** URL de la imagen miniatura del juego */
    @ColumnInfo(name = "thumbnail_url")
    val thumbnailUrl: String? = null,

    /** Tipo de archivo: HTML, PYTHON, JAVA */
    @ColumnInfo(name = "file_type")
    val fileType: String = "HTML",

    /** Si el juego está disponible para los usuarios */
    @ColumnInfo(name = "is_active", defaultValue = "1")
    val isActive: Boolean = true,

    /** ID del administrador que subió el juego */
    @ColumnInfo(name = "uploaded_by")
    val uploadedBy: Long? = null,

    /** Número de veces que se ha jugado */
    @ColumnInfo(name = "play_count", defaultValue = "0")
    val playCount: Int = 0,

    /** Calificación promedio (1-5) */
    @ColumnInfo(name = "rating", defaultValue = "0.0")
    val rating: Float = 0f,

    /** Timestamp de subida */
    @ColumnInfo(name = "uploaded_at")
    val uploadedAt: Long = System.currentTimeMillis()
)